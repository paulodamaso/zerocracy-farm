/*
 * Copyright (c) 2016-2019 Zerocracy
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
package com.zerocracy.radars.github;

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.zerocracy.Farm;
import com.zerocracy.claims.ClaimOut;
import com.zerocracy.entry.ClaimsOf;
import java.io.IOException;
import javax.json.JsonObject;

/**
 * Release rebound.
 *
 * @since 1.0
 */
public final class RbRelease implements Rebound {
    /**
     * Release JSON key.
     */
    private static final String JSON_KEY = "release";

    @Override
    public String react(final Farm farm, final Github github,
        final JsonObject event) throws IOException {
        final String answer;
        if (event.containsKey(RbRelease.JSON_KEY)) {
            final JsonObject release = event.getJsonObject(RbRelease.JSON_KEY);
            final String tag = release.getString("tag_name");
            final Repo repo = github.repos().get(
                new Coordinates.Simple(
                    event.getJsonObject("repository")
                        .getString("full_name")
                )
            );
            new ClaimOut()
                .type("Release was published")
                .param("tag", tag)
                .param("repo", repo.coordinates().toString())
                .param("date", release.getString("published_at"))
                .postTo(new ClaimsOf(farm, new GhProject(farm, repo)));
            answer = String.format(
                "Release published: %d (tag: %s)",
                release.getInt("id"),
                tag
            );
        } else {
            answer = "Not a release event";
        }
        return answer;
    }
}
