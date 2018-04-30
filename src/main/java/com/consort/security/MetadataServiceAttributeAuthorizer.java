package com.consort.security;

import org.pac4j.core.authorization.authorizer.RequireAnyAttributeAuthorizer;

public class MetadataServiceAttributeAuthorizer extends RequireAnyAttributeAuthorizer {

    public MetadataServiceAttributeAuthorizer(final String attribute, final String valueToMatch) {
        super(valueToMatch);
        setElements(attribute);
    }
}