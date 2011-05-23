package org.socialmusicdiscovery.server.business.repository.core;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.socialmusicdiscovery.server.business.model.core.ImageEntity;
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
	public Collection<ImageEntity> findByArtistWithRelations(String artistId,
			Collection<String> mandatoryRelations,
			Collection<String> optionalRelations) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ImageEntity> findByReleaseWithRelations(String releaseId,
			Collection<String> mandatoryRelations,
			Collection<String> optionalRelations) {
		// TODO Auto-generated method stub
		return null;
	}

}
