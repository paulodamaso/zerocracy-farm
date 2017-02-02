/**
 * Copyright (c) 2016 Zerocracy
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
package com.zerocracy.radars;

import com.zerocracy.pm.ClaimOut;

/**
 * Claim on question.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class ClaimOnQuestion {

    /**
     * The question.
     */
    private final Question question;

    /**
     * Ctor.
     * @param qtn Question
     */
    public ClaimOnQuestion(final Question qtn) {
        this.question = qtn;
    }

    /**
     * Build claim.
     * @return Claim
     */
    public ClaimOut claim() {
        final ClaimOut claim;
        if (this.question.matches()) {
            claim = new ClaimOut()
                .type(this.question.code())
                .params(this.question.params());
        } else {
            claim = new ClaimOut()
                .type("notify")
                .param("message", this.question.help());
        }
        return claim;
    }
}