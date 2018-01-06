/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.cloudbees.jenkins.plugins.bitbucket;

import com.cloudbees.jenkins.plugins.bitbucket.credentials.BitbucketPersonalAccessTokenCredentials;
import com.cloudbees.jenkins.plugins.bitbucket.endpoints.AbstractBitbucketEndpoint;
import com.cloudbees.jenkins.plugins.bitbucket.endpoints.BitbucketEndpointConfiguration;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsMatcher;
import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardUsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.Queue;
import hudson.model.queue.Tasks;
import hudson.security.ACL;
import jenkins.scm.api.SCMSourceOwner;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Utility class for common code accessing credentials
 *
 * This is not provided as API and do not use from other plugins.
 */
@Restricted(NoExternalUse.class)
public class BitbucketCredentials {
    private BitbucketCredentials() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * @return the matcher to filter credentials applicable to Bitbucket Cloud.
     */
    @NonNull
    public static CredentialsMatcher getInstanceMatcherForCloud() {
        return CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class);
    }

    /**
     * @return the matcher to filter credentials applicable to Bitbucket Server.
     */
    @NonNull
    public static CredentialsMatcher getInstanceMatcherForServer() {
        return CredentialsMatchers.anyOf(
                CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class),
                CredentialsMatchers.instanceOf(BitbucketPersonalAccessTokenCredentials.class)
        );
    }

    /**
     * @return the matcher to filter credentials applicable either to Bitbucket Cloud or Bitbucket Server.
     */
    @NonNull
    public static CredentialsMatcher getInstanceMatcherForAny() {
        return CredentialsMatchers.anyOf(
                CredentialsMatchers.instanceOf(StandardUsernamePasswordCredentials.class),
                CredentialsMatchers.instanceOf(BitbucketPersonalAccessTokenCredentials.class)
        );
    }

    /**
     * @return the matcher to filter credentials applicable to the url.
     */
    @NonNull
    public static CredentialsMatcher getInstanceMatcherForUrl(String url) {
        AbstractBitbucketEndpoint endpoint = BitbucketEndpointConfiguration.get().findEndpoint(url);
        return (endpoint != null)
                ? endpoint.getDescriptor().getCredentialsMatcher()
                : getInstanceMatcherForAny();
    }

    /**
     * Look up the credentials from the id.
     *
     * Use appropriate matcher to filter applicable credentials.
     *
     * @param serverUrl the URL for the repository.
     * @param context the item accessing the repository.
     * @param id the id to look up.
     * @param instanceMatcher the matcher to filter applicable credentials.
     * @return the credentials
     * @see #getInstanceMatcherForAny()
     * @see #getInstanceMatcherForCloud()
     * @see #getInstanceMatcherForServer()
     */
    @CheckForNull
    public static Credentials lookupCredentials(@CheckForNull String serverUrl,
                                                               @CheckForNull SCMSourceOwner context,
                                                               @CheckForNull String id,
                                                               @NonNull CredentialsMatcher instanceMatcher) {
        if (StringUtils.isNotBlank(id) && context != null) {
            return CredentialsMatchers.firstOrNull(
                    CredentialsProvider.lookupCredentials(
                            StandardCredentials.class,
                            context,
                            context instanceof Queue.Task
                                    ? Tasks.getDefaultAuthenticationOf((Queue.Task) context)
                                    : ACL.SYSTEM,
                            URIRequirementBuilder.fromUri(serverUrl).build()
                    ),
                    CredentialsMatchers.allOf(
                            CredentialsMatchers.withId(id),
                            instanceMatcher
                    )
            );
        }
        return null;
    }

}
