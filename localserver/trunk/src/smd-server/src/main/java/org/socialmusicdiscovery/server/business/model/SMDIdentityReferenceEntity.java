package org.socialmusicdiscovery.server.business.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.annotation.*;

@javax.persistence.Entity
@Table(name = "smdidentity_references")
public class SMDIdentityReferenceEntity implements SMDIdentityReference {
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface ReferenceType {
        public Class type();
    };

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String type;

    public SMDIdentityReferenceEntity() {
    }

    public SMDIdentityReferenceEntity(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public static SMDIdentityReference forEntity(SMDIdentity entity) {
        return new SMDIdentityReferenceEntity(entity.getId(), typeForClass(entity.getClass()));
    }
    public static String typeForClass(Class cls) {
        Annotation annotation = cls.getAnnotation(ReferenceType.class);
        if(annotation instanceof ReferenceType) {
            return ((ReferenceType)annotation).type().getSimpleName();
        }
        throw new RuntimeException("Unsupported SMDIdentity: "+cls.getSimpleName());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SMDIdentityReferenceEntity)) return false;

        return id.equals(((SMDIdentityReferenceEntity) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
