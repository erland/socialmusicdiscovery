package org.socialmusicdiscovery.server.business.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class SMDEntity<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
