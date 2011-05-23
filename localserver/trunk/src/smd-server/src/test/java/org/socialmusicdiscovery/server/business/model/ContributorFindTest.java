/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.business.model;

import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

public class ContributorFindTest extends BaseTestCase {
    @BeforeClass
    public void setUpClass() {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        updateSearchRelations();
    }

    @Test
    public void testFind() {
        Collection<ReleaseEntity> releases = releaseRepository.findByName("The Bodyguard (Original Soundtrack Album)");
        assert releases.size()==1;
        Release release = releases.iterator().next();

        Collection<ArtistEntity> artists = artistRepository.findByName("Whitney Houston");
        assert artists.size()==1;
        Artist artist = artists.iterator().next();

        em.clear();
        Collection<ContributorEntity> contributors = contributorRepository.findAll();
        assert contributors.size()==18;

        em.clear();
        contributors = contributorRepository.findAllWithRelations(Arrays.asList("artist"),null);
        assert contributors.size()==18;

        em.clear();
        Contributor contributor = contributorRepository.findById(contributors.iterator().next().getId());
        assert contributor!=null;

        contributors = contributorRepository.findByArtistWithRelations(artist.getId(),null,null);
        assert contributors.size()==3;

        contributors = contributorRepository.findByReleaseWithRelations(release.getId(),null,null);
        // There are no contributors on the release level
        assert contributors.size()==0;
    }
}
