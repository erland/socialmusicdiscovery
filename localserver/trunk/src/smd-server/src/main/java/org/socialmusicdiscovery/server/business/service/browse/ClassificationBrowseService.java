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

package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;

import java.util.Collection;

public class ClassificationBrowseService extends AbstractBrowseService implements BrowseService<ClassificationEntity> {
    @Override
    public ResultItem<ClassificationEntity> findById(String id) {
        return findById(ClassificationEntity.class, "Classification", id);
    }

    public Integer findChildrenCount(Collection<String> criteriaList) {
        return findChildrenCount(ClassificationEntity.class, "Classification", "classification", criteriaList);
    }

    public Result<ClassificationEntity> findChildren(Collection<String> criteriaList, Collection<String> sortCriteriaList, Integer firstItem, Integer maxItems, Boolean returnChildCounters) {
        Result<ClassificationEntity> result = super.findChildren(ClassificationEntity.class, "Classification", "classification", "e.sortAs", criteriaList, sortCriteriaList,
                new SortKeyProvider() {
                    @Override
                    public String getSortKey(Object item) {
                        return ((ClassificationEntity) item).getSortAs().substring(0, 1);
                    }
                },
                firstItem, maxItems, returnChildCounters);
        result.setAlphabetic(true);
        return result;
    }

    @Override
    protected Boolean getPlayable() {
        return false;
    }

    @Override
    public String getObjectType() {
        return "Classification";
    }
}
