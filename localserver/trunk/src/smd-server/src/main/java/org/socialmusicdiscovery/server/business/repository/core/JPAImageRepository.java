package org.socialmusicdiscovery.server.business.repository.core;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.Query;


import org.socialmusicdiscovery.server.business.model.AbstractSMDIdentityEntity;
import org.socialmusicdiscovery.server.business.model.core.ImageEntity;
import org.socialmusicdiscovery.server.business.model.core.Release;
import org.socialmusicdiscovery.server.business.model.core.ReleaseEntity;
import org.socialmusicdiscovery.server.business.repository.AbstractJPASMDIdentityRepository;

import com.google.inject.Inject;

public class JPAImageRepository extends AbstractJPASMDIdentityRepository<ImageEntity> implements ImageRepository {

	private ImageRepository imageRepository;

	@Inject
	public JPAImageRepository(EntityManager em, ImageRepository imageRepository) {
		super(em);
		this.imageRepository = imageRepository;
	}


	@Override
	public Collection<ImageEntity> findByRelease(Release release) {
		return(this.findByRelatedToSMDEntity((AbstractSMDIdentityEntity) release));
	}
	
	@Override
	public Collection<ImageEntity> findByReleaseId(String releaseId) {
		return(this.findByRelatedToSMDId(releaseId));
	}

	@Override
	public Collection<ImageEntity> findBySMDId(String smdId) {
        Query query = entityManager.createQuery("from ImageEntity where id=:smdId");
        query.setParameter("smdId",smdId);
        return query.getResultList();
	}
	
	@Override
	public Collection<ImageEntity> findByRelatedToSMDId(String relatedToSMDId) {
        Query query = entityManager.createQuery("from ImageEntity where related_to_id=:relatedToSMDId");
        query.setParameter("relatedToSMDId",relatedToSMDId);
        return query.getResultList();
	}

	@Override
	public Collection<ImageEntity> findByRelatedToSMDIdAndType(String relatedToSMDId, String type) {
        Query query = entityManager.createQuery("from ImageEntity where related_to_id=:relatedToSMDId and type=:type");
        query.setParameter("relatedToSMDId",relatedToSMDId);
        query.setParameter("type",type);
        return query.getResultList();
	}

	@Override
	public Collection<ImageEntity> findAll() {
        Query query = entityManager.createQuery("from ImageEntity");
        return query.getResultList();
	}

	@Override
	public Collection<ImageEntity> findByRelatedToSMDEntity(AbstractSMDIdentityEntity relatedSMDEntity) {
        Query query = entityManager.createQuery("from ImageEntity where relatedTo=:smdEntity");
        query.setParameter("smdEntity", relatedSMDEntity.getReference() );
        return query.getResultList();
	}
	
    public void remove(ImageEntity entity) {
        super.remove(entity);
    }
}