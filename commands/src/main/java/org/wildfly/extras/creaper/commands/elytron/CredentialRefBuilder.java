/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.extras.creaper.commands.elytron;

import java.util.HashMap;
import java.util.Map;

import org.wildfly.extras.creaper.core.online.operations.Values;

public final class CredentialRefBuilder {
    private String alias;
    private String type;
    private String store;
    private String clearText;

    public CredentialRefBuilder alias(String alias) {
        this.alias = alias;
        return this;
    }

    public CredentialRefBuilder type(String type) {
        this.type = type;
        return this;
    }

    public CredentialRefBuilder store(String store) {
        this.store = store;
        return this;
    }

    public CredentialRefBuilder clearText(String clearText) {
        this.clearText = clearText;
        return this;
    }

    public Values build() {
        final Map<String, String> credentialReference = new HashMap<String, String>();

        if (isNotBlank(alias)) {
            credentialReference.put("alias", alias);
        }
        if (isNotBlank(type)) {
            credentialReference.put("type", type);
        }
        if (isNotBlank(store)) {
            credentialReference.put("store", store);
        }
        if (isNotBlank(clearText)) {
            credentialReference.put("clear-text", clearText);
        }

        return Values.fromMap(credentialReference);
    }

    private static boolean isNotBlank(String str) {
        return (str != null && str.length() > 0);
    }
}
