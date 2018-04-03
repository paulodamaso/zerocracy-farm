/**
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.pings;

import com.zerocracy.Farm;
import java.io.IOException;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoCheckedScalar;
import org.cactoos.scalar.SolidScalar;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

/**
 * Pings.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @author Kirill (g4s8.public@gmail.com)
 * @version $Id$
 * @since 0.21
 */
public final class Pings {
    /**
     * Claim job param.
     */
    private static final String CLAIM = "claim";
    /**
     * Quartz group.
     */
    private static final String GROUP = "pings";
    /**
     * Farm.
     */
    private final IoCheckedScalar<Scheduler> quartz;
    /**
     * Ctor.
     * @param farm Farm
     */
    public Pings(final Farm farm) {
        this.quartz = new IoCheckedScalar<>(
            new SolidScalar<>(new Pings.Quartz(farm))
        );
    }

    /**
     * Start it.
     * @throws IOException If fails
     */
    public void start() throws IOException {
        this.start(
            "minute",
            "Ping",
            SimpleScheduleBuilder.repeatMinutelyForever(1)
        );
        this.start(
            "hour",
            "Ping hourly",
            SimpleScheduleBuilder.repeatHourlyForever(1)
        );
        this.start(
            "day",
            "Ping daily",
            // @checkstyle MagicNumberCheck (1 line)
            SimpleScheduleBuilder.repeatHourlyForever(24)
        );
    }

    /**
     * Start ping with name, type and schedule.
     * @param name Job name
     * @param claim Claim type
     * @param schedule Job schedule
     * @throws IOException If fails
     */
    private void start(final String name, final String claim,
        final SimpleScheduleBuilder schedule) throws IOException {
        try {
            this.quartz.value().scheduleJob(
                JobBuilder.newJob(Ping.class)
                    .usingJobData(Pings.CLAIM, claim)
                    .withIdentity(name, Pings.GROUP)
                    .build(),
                TriggerBuilder.newTrigger()
                    .forJob(name)
                    .withSchedule(schedule)
                    .startNow()
                    .build()
            );
        } catch (final SchedulerException err) {
            throw new IOException(err);
        }
    }

    /**
     * Quartz scalar.
     */
    private static final class Quartz implements Scalar<Scheduler> {
        /**
         * Farm.
         */
        private final Farm farm;
        /**
         * Ctor.
         * @param farm Farm
         */
        private Quartz(final Farm farm) {
            this.farm = farm;
        }

        @Override
        public Scheduler value() throws Exception {
            final Scheduler scheduler =
                StdSchedulerFactory.getDefaultScheduler();
            scheduler.setJobFactory(
                new Pings.Factory(this.farm, new SimpleJobFactory())
            );
            return scheduler;
        }
    }

    /**
     * Job quartz factory.
     */
    private static final class Factory implements JobFactory {
        /**
         * Farm.
         */
        private final Farm farm;
        /**
         * Fallback factory.
         */
        private final JobFactory fallback;
        /**
         * Ctor.
         * @param farm Farm
         * @param fallback Fallback factory
         */
        Factory(final Farm farm, final JobFactory fallback) {
            this.farm = farm;
            this.fallback = fallback;
        }

        @Override
        public Job newJob(final TriggerFiredBundle bundle,
            final Scheduler scheduler) throws SchedulerException {
            final Job job;
            if (Ping.class.equals(bundle.getJobDetail().getJobClass())) {
                job = new Ping(this.farm);
            } else {
                job = this.fallback.newJob(bundle, scheduler);
            }
            return job;
        }
    }
}
