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
		return(this.findBySmdEntity((AbstractSMDIdentityEntity) release));
	}
	
	@Override
	public Collection<ImageEntity> findByReleaseId(String releaseId) {
		return(this.findBySmdId(releaseId));
	}

	@Override
	public Collection<ImageEntity> findBySmdId(String smdId) {
        Query query = entityManager.createQuery("from ImageEntity where related_to_id=:smdId");
        query.setParameter("smdId",smdId);
        return query.getResultList();
	}
	
	@Override
	public Collection<ImageEntity> findBySmdEntity(AbstractSMDIdentityEntity smdEntity) {
        Query query = entityManager.createQuery("from ImageEntity where relatedTo=:smdEntity");
        query.setParameter("smdEntity", smdEntity.getReference() );
        return query.getResultList();
	}
	
    public void remove(ImageEntity entity) {
        super.remove(entity);
    }
}