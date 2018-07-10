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
package com.zerocracy.tk;

import com.zerocracy.Farm;
import java.io.IOException;
import org.cactoos.list.ListOf;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * View of application pages.
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @version $Id$
 * @since 0.26
 * @todo #1142:30min This class should be tested that it actually forwards
 *  the call to the TkApp with the url provided by the user, and that the
 *  resulting page is an xml or html, depending on the method.
 */
public final class View {

    /**
     * Farm to use for viewing.
     */
    private final Farm farm;

    /**
     * URL to view.
     */
    private final String url;

    /**
     * Constructor.
     * @param farm Farm to use
     * @param url URL to use
     */
    public View(final Farm farm, final String url) {
        this.farm = farm;
        this.url = url;
    }

    /**
     * XML view of given page.
     * @return XML view
     * @throws IOException In case of error
     */
    public String xml() throws IOException {
        return this.page(
            "Some XML rendering user Agent",
            "application/vnd.zerocracy+xml"
        );
    }

    /**
     * HTML view of given page.
     * @return HTML view
     * @throws IOException In case of error
     */
    public String html() throws IOException {
        return this.page(
            // @checkstyle LineLength (1 line)
            "Mozilla/5.0 (X11; Linux x86_64; rv:62.0) Gecko/20100101 Firefox/62.0",
            "text/html,application/xhtml+xml,application/xml"
        );
    }

    /**
     * Generate view of page using provided user agent.
     * @param agent User agent to use
     * @param accept Content types in Accept header
     * @return Generated page
     * @throws IOException In case of error
     */
    private String page(final String agent, final String accept)
        throws IOException {
        return new RsPrint(
            new TkApp(this.farm).act(
                new RqWithUser(
                    this.farm,
                    new RqFake(
                        new ListOf<>(
                            String.format("GET %s", this.url),
                            "Host: www.example.com",
                            String.format("Accept: %s", accept),
                            String.format("User-Agent: %s", agent)
                        ),
                        ""
                    )
                )
            )
        ).printBody();
    }
}
