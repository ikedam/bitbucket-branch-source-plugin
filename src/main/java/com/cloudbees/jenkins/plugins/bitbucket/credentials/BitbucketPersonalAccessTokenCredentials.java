/*
 * The MIT License
 *
 * Copyright (c) 2018 IKEDA Yasuyuki
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

package com.cloudbees.jenkins.plugins.bitbucket.credentials;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import com.cloudbees.plugins.credentials.CredentialsScope;
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials;

import hudson.Extension;
import hudson.Util;
import hudson.util.FormValidation;

/**
 * Credentials using Personal Access Token feature of Bitbucket Server.
 *
 * https://confluence.atlassian.com/bitbucketserver055/personal-access-tokens-940682155.html
 */
public class BitbucketPersonalAccessTokenCredentials extends BaseStandardCredentials {
    private static final long serialVersionUID = -4170745589344588679L;

    @Nonnull
    private final String token;

    /**
     * ctor.
     *
     * @param scope             the scope.
     * @param id                the id.
     * @param description       the description.
     * @param token             the personal access token.
     */
    @DataBoundConstructor
    public BitbucketPersonalAccessTokenCredentials(
        @CheckForNull CredentialsScope scope,
        @CheckForNull String id,
        @CheckForNull String description,
        @CheckForNull String token
    ) {
        super(scope, id, description);
        this.token = Util.fixNull(token).trim();
    }

    /**
     * @return the personal access token.
     */
    @Nonnull
    public String getToken() {
        return token;
    }

    @Extension
    public static class DescriptorImpl extends BaseStandardCredentials.BaseStandardCredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.BitbucketPersonalAccessTokenCredentials_DisplayName();
        }

        @Restricted(NoExternalUse.class)
        public FormValidation doCheckToken(@QueryParameter String token) {
            if (StringUtils.isBlank(token)) {
                return FormValidation.error(Messages.BitbucketPersonalAccessTokenCredentials_Required());
            }
            return FormValidation.ok();
        }
    }
}
