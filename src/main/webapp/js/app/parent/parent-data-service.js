angular.module('parentApp').service('parentDataService', function ($http, $q, CacheFactory, msgBus, contextService) {
    var PRACTICES_SUMMARY_KEY = 'practicesSummaryKeyParentApp';
    var PRACTICES_BY_GROUPS_KEY = 'practicesByGroupsKeyParentApp';
    var PRACTICES_BY_CHILDS_KEY = 'practicesByChildsKeyParentApp';
    var ALL_CHILDS_KEY = 'parentAllChildKeyParentApp';
    var ALL_GROUPS_KEY = 'parentAllGroupKeyParentApp';
    var PRACTICE_KEY = 'practiceKeyParentApp_';
    var PARENT_PROFILE_KEY = 'parentProfileKey';

    CacheFactory('parentAppCache', {
        maxAge: 15 * 60 * 1000, // Items added to this cache expire after 15 minutes
        cacheFlushInterval: 15 * 60 * 1000, // This cache will clear itself every 15 min
        deleteOnExpire: 'aggressive' // Items will be deleted from this cache when they expire
    });

    var parentAppCache = CacheFactory.get('parentAppCache');

    // avoiding replacing the practice since I want to improve performance, creating all the questions again could be performance issue
    function newPracticeSummary(practice) {
        return {
            description: practice.description,
            id: practice.id,
            name: practice.name,
            questionCount: practice.questions.length,
            questionsType: practice.questionsType,
            useTTS: practice.useTTS,
            createdTime: practice.createdTime,
            creatorId: practice.creatorId,
            creatorFullName: practice.creatorFullName
        }
    }

    var service =  {
        getPracticesSummary: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            if (parentAppCache.get(PRACTICES_SUMMARY_KEY)) {
                console.log('use cache, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                deferred.resolve(deepCopyCollection(parentAppCache.get(PRACTICES_SUMMARY_KEY)));
            } else {
                $http.get('/parent/practicesSummary').success(function (data) {
                    console.log('time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    parentAppCache.put(PRACTICES_SUMMARY_KEY, data);
                    deferred.resolve(deepCopyCollection(data));
                }).error(function () {
                    displayError();
                    deferred.reject();
                });
            }
            return deferred.promise;
        },

        getPracticesByGroups: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            if (parentAppCache.get(PRACTICES_BY_GROUPS_KEY)) {
                console.log('use cache, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                deferred.resolve(deepCopyCollection(parentAppCache.get(PRACTICES_BY_GROUPS_KEY)));
            } else {
                $http.get('/parent/' + contextService.getUserId() + '/practicesByGroups').success(function (data) {
                    console.log('time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    parentAppCache.put(PRACTICES_BY_GROUPS_KEY, data);
                    deferred.resolve(deepCopyCollection(data));
                }).error(function () {
                    displayError();
                    deferred.reject();
                });
            }
            return deferred.promise;
        },

        getPracticesByChilds: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            if (parentAppCache.get(PRACTICES_BY_CHILDS_KEY)) {
                console.log('use cache, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                deferred.resolve(deepCopyCollection(parentAppCache.get(PRACTICES_BY_CHILDS_KEY)));
            } else {
                $http.get('/parent/' + contextService.getUserId() + '/practicesByChilds').success(function (data) {
                    console.log('time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    parentAppCache.put(PRACTICES_BY_CHILDS_KEY, data);
                    deferred.resolve(deepCopyCollection(data));
                }).error(function () {
                    displayError();
                    deferred.reject();
                });
            }
            return deferred.promise;
        },

        getPractice: function (practiceId) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            var practiceCacheId = PRACTICE_KEY + practiceId;
            var practice = parentAppCache.get(practiceCacheId);
            if (practice) {
                console.log('use cache, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                deferred.resolve(angular.copy(practice));
            } else {
                $http.get('/practice/' + practiceId).success(function (data) {
                    if (data.error) {
                        if (data.errorMessage == "entityNotFound") {
                            toastr.error('התרגיל נמחק או שאינו קיים');
                        }
                        deferred.reject();
                        return;
                    }
                    console.log('time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    parentAppCache.put(practiceCacheId, data.responseObject);
                    deferred.resolve(angular.copy(data.responseObject));
                }).error(function () {
                    displayError();
                    deferred.reject();
                });
            }
            return deferred.promise;
        },

        createPractice: function (practice) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.put('/practice', practice).success(function(result) {
                var createdPractice = result.responseObject;
                console.log('create practice with id ' + createdPractice.id + ' took : ' + (new Date().getTime() - start) + 'ms');

                var practiceCacheId = PRACTICE_KEY + createdPractice.id;
                parentAppCache.put(practiceCacheId, createdPractice);

                var practicesSummary = parentAppCache.get(PRACTICES_SUMMARY_KEY);
                if (practicesSummary) {
                    practicesSummary.unshift(newPracticeSummary(createdPractice));
                }

                clearRelatedPracticeCache();
                deferred.resolve(angular.copy(createdPractice));
                msgBus.emitMsg(PRACTICE_CREATED_EVENT);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        updatePractice: function (practice) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.post('/practice/' + practice.id, practice).success(function(result) {
                console.log('update practice with id ' + practice.id + ' took : ' + (new Date().getTime() - start) + 'ms');
                var updatedPractice = result.responseObject;

                var practiceCacheId = PRACTICE_KEY + practice.id;
                parentAppCache.put(practiceCacheId, updatedPractice);

                var practicesSummary = parentAppCache.get(PRACTICES_SUMMARY_KEY);
                if (practicesSummary) {
                    for(var i=0; i<practicesSummary.length; i++) {
                        var practiceSummary = practicesSummary[i];
                        if (practiceSummary.id == updatedPractice.id) {
                            practicesSummary[i] = newPracticeSummary(updatedPractice);
                            break;
                        }
                    }
                }

                clearRelatedPracticeCache();
                deferred.resolve(angular.copy(updatedPractice));
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        addQuestionsOfPractice: function (trgPractice, srcPractice) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.post('/practice/' + trgPractice.id + '/mergeQuestions', srcPractice.id).success(function(result) {
                var updatedPractice = result.responseObject;
                console.log('update practice with id ' + updatedPractice.id + ' took : ' + (new Date().getTime() - start) + 'ms');

                var practiceCacheId = PRACTICE_KEY + updatedPractice.id;
                parentAppCache.put(practiceCacheId, updatedPractice);

                var practicesSummary = parentAppCache.get(PRACTICES_SUMMARY_KEY);
                if (practicesSummary) {
                    for(var i=0; i<practicesSummary.length; i++) {
                        var practiceSummary = practicesSummary[i];
                        if (practiceSummary.id == updatedPractice.id) {
                            practicesSummary[i] = newPracticeSummary(updatedPractice);
                            break;
                        }
                    }
                }

                // clearRelatedPracticeCache(); don't need to clear this cache as this operation is just add some question to practice
                deferred.resolve(angular.copy(updatedPractice));
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        duplicatePractice: function (srcPracticeId, newPracticeName) {
            var deferred = $q.defer();
            var start = new Date().getTime();
            var practiceDto = {name: newPracticeName};

            $http.post('/practice/' + srcPracticeId + '/duplicatePractice', practiceDto).success(function(result) {
                var createdPractice = result.responseObject;
                console.log('create new practice with id ' + createdPractice.id + ' took : ' + (new Date().getTime() - start) + 'ms');

                var practiceCacheId = PRACTICE_KEY + createdPractice.id;
                parentAppCache.put(practiceCacheId, createdPractice);

                var practicesSummary = parentAppCache.get(PRACTICES_SUMMARY_KEY);
                if (practicesSummary) {
                    practicesSummary.unshift(newPracticeSummary(createdPractice));
                }

                clearRelatedPracticeCache();
                deferred.resolve(angular.copy(createdPractice));
                msgBus.emitMsg(PRACTICE_CREATED_EVENT);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        getTtsGenerationState: function (practiceId) {
            var deferred = $q.defer();
            $http.get('/practice/' + practiceId + "/ttsState").success(function(result) {
                var responseObj = result.responseObject;
                if (responseObj.summaryState == 'DONE' && responseObj.questionIdToTtsUrl) {
                    var questionIdToTtsUrl = responseObj.questionIdToTtsUrl;
                    var practiceCacheId = PRACTICE_KEY + responseObj.practiceId;
                    var existPractice = parentAppCache.get(practiceCacheId);
                    for (var i=0; i<existPractice.questions.length; i++) {
                        var question = existPractice.questions[i];
                        question.ttsUrl = questionIdToTtsUrl[question.id];
                    }
                }
                deferred.resolve(angular.copy(responseObj));
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        deletePractice: function (practiceId) {
            var deferred = $q.defer();
            $http.delete('/practice/' + practiceId).success(function(result) {
                var serverPracticeId = result.responseObject.id;
                var practiceName = result.responseObject.name;
                console.log("practice '" + practiceName + "' with id: " + serverPracticeId + " was deleted");

                var practiceCacheId = PRACTICE_KEY + serverPracticeId;
                var deletedPractice = parentAppCache.remove(practiceCacheId); // not sure if this practice exist in the cache, maybe only the summary exist

                var practicesSummary = parentAppCache.get(PRACTICES_SUMMARY_KEY);
                if (practicesSummary) {
                    for(var i=0; i<practicesSummary.length; i++) {
                        var practiceSummary = practicesSummary[i];
                        if (practiceSummary.id == serverPracticeId) {
                            practicesSummary.splice(i, 1);
                            break;
                        }
                    }
                }

                clearRelatedPracticeCache(serverPracticeId);
                deferred.resolve();
                msgBus.emitMsg(PRACTICE_DELETED_EVENT, practiceId);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        getParentProfile: function (forceRefresh) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            var parentProfile = parentAppCache.get(PARENT_PROFILE_KEY);
            if (parentProfile && !(forceRefresh == true)) { // undefined or false
                console.log('getParentProfile, use cache, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                deferred.resolve(angular.copy(parentProfile));
            } else {
                $http.get('/parent/profile').then(function (result) {
                    console.log('getParentProfile, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    parentAppCache.put(PARENT_PROFILE_KEY, result.data);
                    deferred.resolve(angular.copy(result.data));
                }, function () {
                    toastr.error('אירעה שגיאת רשת, אנא נסו מאוחר יותר');
                    deferred.reject();
                });
            }
            return deferred.promise;
        },

        updateParentProfile: function (parentProfile) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.post('/parent/' + parentProfile.parentId + '/profile', parentProfile).success(function(result) {
                console.log('update parentProfile with id ' + parentProfile.parentId + ' took : ' + (new Date().getTime() - start) + 'ms');
                var updatedProfile = result.responseObject;
                parentAppCache.put(PARENT_PROFILE_KEY, updatedProfile);
                var updatedProfileCopy = angular.copy(updatedProfile);
                deferred.resolve(updatedProfileCopy);
                msgBus.emitMsg(PARENT_PROFILE_CHANGE_EVENT, updatedProfileCopy);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        getAllChilds: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            var allChilds = parentAppCache.get(ALL_CHILDS_KEY);
            if (allChilds) {
                console.log('getChilds, use cache, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                var childsCopy = deepCopyCollection(allChilds);
                deferred.resolve(childsCopy);
            } else {
                $http.get('/parent/childs').success(function (data) {
                    console.log('getChilds, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    parentAppCache.put(ALL_CHILDS_KEY, data);
                    var childsCopy = deepCopyCollection(data);
                    deferred.resolve(childsCopy);
                }, function () {
                    toastr.error('אירעה שגיאת רשת, אנא נסו מאוחר יותר');
                    deferred.reject();
                });
            }
            return deferred.promise;
        },

        createChild: function (child) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.put('/parent/addChild', child).success(function(result) {
                var createdChild = result.responseObject;
                console.log('create child with id ' + createdChild.id + ' took : ' + (new Date().getTime() - start) + 'ms');
                var allChilds = parentAppCache.get(ALL_CHILDS_KEY);
                allChilds.unshift(createdChild);
                deferred.resolve(angular.copy(createdChild));
                parentAppCache.remove(ALL_GROUPS_KEY);   // simple handling for the child in the 'myChildInGroup' tab
                msgBus.emitMsg('childCreated');
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        updateChild: function (child) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.post('/parent/updateChild', child).success(function(result) {
                var updatedChild = result.responseObject;
                console.log('update child with id ' + updatedChild.id + ' took : ' + (new Date().getTime() - start) + 'ms');
                var allChilds = parentAppCache.get(ALL_CHILDS_KEY);
                for (var j = 0; j < allChilds.length; j++) {
                    var currentChild = allChilds[j];
                    if (currentChild.id == updatedChild.id) {
                        allChilds[j] = updatedChild;
                        break;
                    }
                }
                var copyOfUpdatedChild = angular.copy(updatedChild);
                deferred.resolve(copyOfUpdatedChild);
                parentAppCache.remove(ALL_GROUPS_KEY);   // simple handling for the child in the 'myChildInGroup' tab
                msgBus.emitMsg('childUpdated', copyOfUpdatedChild);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        deleteChild: function (child) {
            var deferred = $q.defer();

            $http.delete('/parent/removeChild/' + child.id).success(function(result) {
                var removedChild = result.responseObject;
                console.log("child '" + removedChild.displayName + "' with id: " + removedChild.id + " was deleted");
                var allChilds = parentAppCache.get(ALL_CHILDS_KEY);
                for (var j = 0; j < allChilds.length; j++) {
                    var currentChild = allChilds[j];
                    if (currentChild.id == removedChild.id) {
                        allChilds.splice(j, 1);
                        break;
                    }
                }
                var copyOfRemovedChild = angular.copy(removedChild);
                deferred.resolve(copyOfRemovedChild);
                parentAppCache.remove(ALL_GROUPS_KEY);   // simple handling for the child in the 'myChildInGroup' tab
                clearRelatedPracticeCache();
                msgBus.emitMsg('childRemoved', copyOfRemovedChild);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        getAllGroups: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
            if (allGroups) {
                console.log('getGroups, use cache, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                var groupsCopy = deepCopyCollection(allGroups);
                deferred.resolve(groupsCopy);
            } else {
                $http.get('/parent/groups').success(function (data) {
                    console.log('getGroups, time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    parentAppCache.put(ALL_GROUPS_KEY, data);
                    var groupsCopy = deepCopyCollection(data);
                    deferred.resolve(groupsCopy);
                }, function () {
                    toastr.error('אירעה שגיאת רשת, אנא נסו מאוחר יותר');
                    deferred.reject();
                });
            }
            return deferred.promise;
        },

        createGroup: function (groupToCreate) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.put('/group', groupToCreate).success(function(result) {
                var createdGroup = result.responseObject;
                console.log('create group with id ' + createdGroup.id + ' took : ' + (new Date().getTime() - start) + 'ms');
                var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
                allGroups.unshift(createdGroup);
                var copyOfCreatedGroup = angular.copy(createdGroup);
                deferred.resolve(copyOfCreatedGroup);
                msgBus.emitMsg('groupCreated');
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        updateGroup: function (groupToUpdate) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.post('/group', groupToUpdate).success(function(result) {
                var updatedGroup = result.responseObject;
                console.log('update group with id ' + updatedGroup.id + ' took : ' + (new Date().getTime() - start) + 'ms');
                var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
                for (var j = 0; j < allGroups.length; j++) {
                    var currentGroup = allGroups[j];
                    if (currentGroup.id == updatedGroup.id) {
                        allGroups[j] = updatedGroup;
                        break;
                    }
                }

                clearRelatedPracticeCache();
                var copyOfUpdatedGroup = angular.copy(updatedGroup);
                deferred.resolve(copyOfUpdatedGroup);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        deleteGroup: function (groupToRemove) {
            var deferred = $q.defer();

            $http.delete('/group/' + groupToRemove.id).success(function(result) {
                var removedGroup = result.responseObject;
                console.log("group '" + removedGroup.name + "' with id: " + removedGroup.id + " was deleted");
                var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
                for (var j = 0; j < allGroups.length; j++) {
                    var currentGroup = allGroups[j];
                    if (currentGroup.id == removedGroup.id) {
                        allGroups.splice(j, 1);
                        break;
                    }
                }

                clearRelatedPracticeCache();
                var copyOfRemovedGroup = angular.copy(removedGroup);
                deferred.resolve(copyOfRemovedGroup);
                msgBus.emitMsg('groupRemoved', copyOfRemovedGroup);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        removeParentFromGroup: function (groupId, parent) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.post('/group/' + groupId + '/removeParents', parent).success(function(result) {
                var updatedGroup = result.responseObject;
                console.log('remove parentId: ' + parent.id + ' from groupId: ' + groupId + ' took : ' + (new Date().getTime() - start) + 'ms');
                var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
                for (var j = 0; j < allGroups.length; j++) {
                    var currentGroup = allGroups[j];
                    if (currentGroup.id == updatedGroup.id) {
                        allGroups[j] = updatedGroup;
                        break;
                    }
                }

                clearRelatedPracticeCache();    // if this parent create and shared practices related to my childs
                var copyOfUpdatedGroup = angular.copy(updatedGroup);
                deferred.resolve(copyOfUpdatedGroup);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        inviteAgainToGroup: function (groupId, parentMember) {
            var deferred = $q.defer();

            $http.post('/group/' + groupId + '/reInvite', [{email: parentMember.email}]).success(function(result) {
                var updatedGroup = result.responseObject.updatedGroup;
                var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
                for (var j = 0; j < allGroups.length; j++) {
                    var currentGroup = allGroups[j];
                    if (currentGroup.id == updatedGroup.id) {
                        allGroups[j] = updatedGroup;
                        break;
                    }
                }
                var copyOfUpdatedGroup = angular.copy(updatedGroup);
                deferred.resolve(copyOfUpdatedGroup);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        setParentToGroupTeacher: function (groupId, parentId) {
            return setParentToGroupRole(groupId, parentId, 'setTeacher');
        },

        setParentToGroupManager: function (groupId, parentId) {
            return setParentToGroupRole(groupId, parentId, 'setManager');
        },

        inviteToGroup: function (updateGroupForInvite) {
            var deferred = $q.defer();

            $http.post('/group/invite', updateGroupForInvite).success(function(result) {
                var responseObj = result.responseObject;
                var updatedGroup = responseObj.updatedGroup;
                var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
                for (var j = 0; j < allGroups.length; j++) {
                    var currentGroup = allGroups[j];
                    if (currentGroup.id == updatedGroup.id) {
                        allGroups[j] = updatedGroup;
                        break;
                    }
                }
                responseObj.updatedGroup = angular.copy(updatedGroup);  // replace the updatedGroup with a copy
                deferred.resolve(responseObj);
            }).error(function(result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        saveChildInGroupData: function (groupId, childsInGroupDtos) {
            var deferred = $q.defer();

            $http.post('/group/' + groupId + '/childInGroup', childsInGroupDtos).success(function(result) {
                var updatedChilds = result.responseObject;

                var updatedGroup;
                var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
                for (var j = 0; j < allGroups.length; j++) {
                    var currentGroup = allGroups[j];
                    if (currentGroup.id == groupId) {
                        currentGroup.myChilds = updatedChilds;
                        updatedGroup = currentGroup;
                        break;
                    }
                }

                clearRelatedPracticeCache();
                var copyOfUpdatedGroup = angular.copy(updatedGroup);
                deferred.resolve(copyOfUpdatedGroup);
            }).error(function (result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        },

        changeChildPassword: function (child) {
            var deferred = $q.defer();

            $http.post('/parent/' + contextService.getUserId() + '/changeChildPw', child).success(function(result) {
                deferred.resolve("success");
            }).error(function (result) {
                handleError(result, deferred);
            });
            return deferred.promise;
        }
    };
    return service;

    function handleError(result, deferred) {
        var msg = result.errorMessage ? result.errorMessage : 'אירעה שגיאה, אנא נסו מאוחר יותר';
        toastr.error(msg);//todo i18n
        deferred.reject(msg);
    }

    function setParentToGroupRole(groupId, parentId, apiName) {
        var deferred = $q.defer();

        $http.post('/group/' + groupId + '/' + apiName + '?parentId=' + parentId).success(function(result) {
            var updatedGroup = result.responseObject;
            var allGroups = parentAppCache.get(ALL_GROUPS_KEY);
            for (var j = 0; j < allGroups.length; j++) {
                var currentGroup = allGroups[j];
                if (currentGroup.id == updatedGroup.id) {
                    allGroups[j] = updatedGroup;
                    break;
                }
            }
            var copyOfUpdatedGroup = angular.copy(updatedGroup);
            deferred.resolve(copyOfUpdatedGroup);
        }).error(function(result) {
            handleError(result, deferred);
        });
        return deferred.promise;
    }

    function displayError() {
        toastr.error('אירעה שגיאה, אנא נסו מאוחר יותר');//todo i18n
    }

    function clearRelatedPracticeCache() {
        parentAppCache.remove(PRACTICES_BY_GROUPS_KEY);
        parentAppCache.remove(PRACTICES_BY_CHILDS_KEY);
    }

});