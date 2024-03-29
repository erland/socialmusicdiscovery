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

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.core.Label;
import org.socialmusicdiscovery.server.business.model.core.LabelEntity;
import org.socialmusicdiscovery.server.business.repository.core.LabelRepository;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;

public class LabelFindTest extends BaseTestCase {
    @Inject
    LabelRepository labelRepository;

    @BeforeClass
    public void setUpClass() {
        loadTestData(getClass().getPackage().getName(),"The Bodyguard.xml");
        updateSearchRelations();
    }

    @Test
    public void testFind() {
        em.clear();
        Collection<LabelEntity> labels = labelRepository.findAll();
        assert labels.size()==1;

        em.clear();
        labels = labelRepository.findAllWithRelations(null, null);
        assert labels.size()==1;

        em.clear();
        Label label = labelRepository.findById(labels.iterator().next().getId());
        assert label != null;

        labels = labelRepository.findByName("Arista");
        assert labels.size()==1;
        assert labels.iterator().next().getName().equals("Arista");

        labels = labelRepository.findByNameWithRelations("Arista", null, null);
        assert labels.size()==1;
        assert labels.iterator().next().getName().equals("Arista");

        labels = labelRepository.findByPartialNameWithRelations("Ari",null,null);
        assert labels.size()==1;
        assert labels.iterator().next().getName().equals("Arista");
    }
}
