var app = angular.module('childApp', ['ngRoute', 'ngMaterial', 'ngMdIcons', 'angular-cache']);

app.run(function ($http, $rootScope, CacheFactory) {
    $http.defaults.cache = CacheFactory('defaultCache', {
        maxAge: 15 * 60 * 1000, // Items added to this cache expire after 15 minutes
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive' // Items will be deleted from this cache when they expire
    });

    $rootScope.isMobileOrTablet = isMobileOrTablet();
});

app.service('dataService', function ($http, $q, CacheFactory, msgBus) {
    var CHILD_PROPERTIES_KEY = 'childProperties';

    var practicesSearch = '';

    CacheFactory('childAppCache', {
        maxAge: 60 * 60 * 1000, // Items added to this cache expire after 60 minutes
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive' // Items will be deleted from this cache when they expire
    });

    var childAppCache = CacheFactory.get('childAppCache');

    return {
        getChildSummary: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.get('/child/practicesSummary', {
                cache: true
            }).success(function (data) {
                console.log('time taken for request: ' + (new Date().getTime() - start) + ' ms');
                deferred.resolve(data);
            });
            return deferred.promise;
        },

        getRecentPracticesIds: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.get('/child/recentPractices', {
                cache: true
            }).success(function (data) {
                console.log('time taken for request: ' + (new Date().getTime() - start) + ' ms');
                deferred.resolve(data);
            });
            return deferred.promise;
        },

        getChildProperties: function () {
            var deferred = $q.defer();
            var start = new Date().getTime();

            if (childAppCache.get(CHILD_PROPERTIES_KEY)) {
                deferred.resolve(angular.copy(childAppCache.get(CHILD_PROPERTIES_KEY)));
            } else {
                $http.get('/child/properties').success(function (data) {
                    console.log('time taken for request: ' + (new Date().getTime() - start) + 'ms');
                    childAppCache.put(CHILD_PROPERTIES_KEY, data);
                    deferred.resolve(angular.copy(data));
                });
            }
            return deferred.promise;
        },

        updateChildProperties: function(newData) {
            var deferred = $q.defer();
            $http.post('/child', newData).then(function (result) {
                var updatedProperties = result.data.responseObject;
                childAppCache.put(CHILD_PROPERTIES_KEY, updatedProperties);
                msgBus.emitMsg('childPropertiesDataMsg', updatedProperties);
                deferred.resolve(updatedProperties);
            }, function (result) {
                $scope.error(result.data.errorMessage ? result.data.errorMessage : 'אירעה שגיאה, אנא נסו מאוחר יותר');//todo i18n
                deferred.reject(result);
            });
            return deferred.promise;
        },

        getPractice: function(practiceId) {
            var deferred = $q.defer();
            var start = new Date().getTime();

            $http.get('/practice/' + practiceId, {
                cache: true
            }).success(function (data) {
                console.log('time taken for request: ' + (new Date().getTime() - start) + ' ms');
                deferred.resolve(data);
            });
            return deferred.promise;
        },

        sendPracticeResult: function(practiceResult) {
            var deferred = $q.defer();
            $http.put('/report/practiceResult', practiceResult).then(function () {
                deferred.resolve();
            });
            return deferred.promise;
        },

        getPracticeSearch: function() {
            return practicesSearch;
        },

        setPracticeSearch: function(searchStr) {
            practicesSearch = searchStr;
        }
    };
});

app.config(['$routeProvider', '$locationProvider',
    function($routeProvider, $locationProvider) {
        $routeProvider.
            when('/all-practices', {
                templateUrl: '/html/childApp/allPractices.html',
                controller: childAllPracticesController
            }).
            when('/', {      // todo - need to find a nicer way to handle two url to same controller
                templateUrl: '/html/childApp/allPractices.html',
                controller: childAllPracticesController
            }).
            when('/recent-practices', {
                templateUrl: '/html/childApp/recentPractices.html',
                controller: childRecentPracticesController
            }).
            when('/practice/:practiceId', {
                templateUrl: '/html/childApp/practice.html',
                controller: childPracticeController
            }).
            otherwise({
                redirectTo: '/'
            });

        // use the HTML5 History API
        $locationProvider.html5Mode(true);
    }]);

app.controller('AppCtrl', ['$scope', '$mdBottomSheet','$mdSidenav', '$mdDialog', '$location', '$timeout', 'msgBus', 'dataService',
    function($scope, $mdBottomSheet, $mdSidenav, $mdDialog, $location, $timeout, msgBus, dataService){
        $scope.searchPracticeStr = '';
        $scope.pageTitle = '';
        $scope.childGender = 'MALE';
        $scope.lockedOpen = true;

        $timeout(function() {   // this is a workaround for chrome bug that the page get only part of the view
            $scope.lockedOpen = false;
        }, 500);

        $timeout(function() {
            $('.full-screen-loader').fadeOut(1000);
            $timeout(function() {
                $('#parent-view-as-child').animate({bottom:'0px'}, 2000);
            }, 2000);
        }, 2000);

        dataService.getChildProperties()
            .then(function (result) {
                $scope.childGender = result.gender;
                msgBus.emitMsg('childPropertiesDataMsg', result);
            }, function () {
                toastr.error('אירעה שגיאת רשת, אנא נסו מאוחר יותר');//todo
            });

        $scope.navigateTo = function(path, fromSideMenu, queryParam) {
            if (queryParam) {
                $location.path(path).search(queryParam);
            } else {
                $location.path(path);
            }
            var sidenav = $mdSidenav('left');
            if (fromSideMenu && sidenav.isOpen()) {
                sidenav.toggle();
            }
        };

        msgBus.onMsg('navigationEvent', function(event, data) {
            $scope.pageTitle = data;
        }, $scope);

        $scope.toggleSidenav = function(menuId) {
            $mdSidenav(menuId).toggle();
        };

        msgBus.onMsg('childPropertiesDataMsg', function(event, data) {
            $scope.userAvatar = data.userAvatar;
            if (isNotBlank(data.firstName) || isNotBlank(data.lastName)) {
                var displayName = '';
                if (isNotBlank(data.firstName)) {
                    displayName = data.firstName.trim();
                }
                if (isNotBlank(data.lastName)) {
                    if (isNotBlank(displayName)) {
                        displayName += ' ' + data.lastName.trim();
                    } else {
                        displayName = data.lastName.trim();
                    }
                }
                if (isBlank(displayName)) {
                    displayName = 'פלוני אלמוני';
                }
            }
            $scope.userFullName = displayName;
        }, $scope);

        $scope.menu = [
            {
                link : '/all-practices',
                title: 'כל התרגילים',
                icon: 'format_list_bulleted'
            },
            {
                link : '/recent-practices',
                title: 'תורגל לאחרונה',
                icon: 'access_time'
            }
        ];
        $scope.admin = [
            //{
            //    link : '',
            //    title: 'Trash',
            //    icon: 'delete'
            //},
            //{
            //    link : 'showListBottomSheet($event)',
            //    title: 'Settings',
            //    icon: 'settings'
            //}
        ];


        $scope.showListBottomSheet = function($event) {
            $scope.alert = '';
            $mdBottomSheet.show({
                template: '<md-bottom-sheet class="md-list md-has-header"> <md-subheader>Settings</md-subheader> <md-list> <md-item ng-repeat="item in items"><md-item-content md-ink-ripple flex class="inset"> <a flex aria-label="{{item.name}}" ng-click="listItemClick($index)"> <span class="md-inline-list-icon-label">{{ item.name }}</span> </a></md-item-content> </md-item> </md-list></md-bottom-sheet>',
                controller: 'ListBottomSheetCtrl',
                targetEvent: $event
            }).then(function(clickedItem) {
                $scope.alert = clickedItem.name + ' clicked!';
            });
        };

        $scope.logout = function() {
            window.location.replace("/user/logout")
        };

        $scope.openProfileDialog = function(ev) {
            $mdDialog.show({
                controller: ChildPropertiesDialogController,
                templateUrl: '/html/childApp/directives/child-properties-dialog.html',
                targetEvent: ev
            })
                .then(function(answer) {
                    $scope.alert = 'You said the information was "' + answer + '".';
                }, function() {
                    $scope.alert = 'You cancelled the dialog.';
                });
        };

        $scope.openMoreMenu = function($mdOpenMenu, ev) {
            originatorEv = ev;
            $mdOpenMenu(ev);
        };

        $scope.showSearch = function() {
            $scope.showSearchFlag = !$scope.showSearchFlag;
            $timeout(function() {
                jQuery('#serarchField').focus();
            }, 50);
        };

        $scope.hideSearch = function() {
            $scope.showSearchFlag = !$scope.showSearchFlag;
            $scope.searchPracticeStr = '';
            msgBus.emitMsg('searchPracticeMsg', $scope.searchPracticeStr);
        };

        $scope.searchSubmit = function(event) {
            var search = $scope.searchPracticeStr.trim();
            if (event.keyCode == 13) { // Enter
                return;
            }

            if (event.keyCode == 27) {  // Esc
                $scope.hideSearch();
                return;
            }

            dataService.setPracticeSearch(search);
            msgBus.emitMsg('searchPracticeMsg', search);
        };
    }]);


function ChildPropertiesDialogController($scope, $mdDialog, dataService) {
    $scope.avatars = [];
    for (var i = 0; i < 16; i++) {
        $scope.avatars[i] = {
            index: i,
            svg: 'avatars:svg-' + (i + 1),
            selected: false
        }
    }

    dataService.getChildProperties().then(function (data) {
        $scope.userProperties = data;
        var selectedIndex = data.userAvatar.substring(data.userAvatar.indexOf('-') +1);
        $scope.avatars[selectedIndex -1].selected = true;
    });

    $scope.selectAvatar = function (allAvatars, selectedIndex) {
        angular.forEach(allAvatars, function (avatar, index) {
            avatar.selected = (selectedIndex == index);
        });
        $scope.userProperties.userAvatar = 'avatars:svg-' + (selectedIndex + 1);
    };

    $scope.cancel = function () {
        $mdDialog.cancel();
    };

    $scope.save = function () {
        dataService.updateChildProperties($scope.userProperties).then(function () {
            $mdDialog.hide();
        }, function () {
            $mdDialog.hide();
        });
    };
}

app.directive('backButton', ['$window', function($window) {
    return {
        restrict: 'A',
        link: function (scope, elem) {
            elem.bind('click', function () {
                $window.history.back();
            });
        }
    };
}]);

app.directive('stopEvent', function () {
    return {
        restrict: 'A',
        link: function (scope, element) {
            element.bind('click', function (e) {
                e.stopPropagation();
            });
        }
    };
});

app.config(function($mdThemingProvider) {
    if (getCookie("child_gender") == 'FEMALE') {
        $mdThemingProvider.theme('default').primaryPalette('pink');
    } else {
        $mdThemingProvider.theme('default').primaryPalette('blue');
    }
});

app.config(function($mdIconProvider) {
    $mdIconProvider
        // linking to https://github.com/google/material-design-icons/tree/master/sprites/svg-sprite
        //
        .iconSet('action', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-action.svg', 24)
        .iconSet('alert', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-alert.svg', 24)
        .iconSet('av', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-av.svg', 24)
        .iconSet('communication', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-communication.svg', 24)
        .iconSet('content', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-content.svg', 24)
        .iconSet('device', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-device.svg', 24)
        .iconSet('editor', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-editor.svg', 24)
        .iconSet('file', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-file.svg', 24)
        .iconSet('hardware', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-hardware.svg', 24)
        .iconSet('image', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-image.svg', 24)
        .iconSet('maps', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-maps.svg', 24)
        .iconSet('navigation', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-navigation.svg', 24)
        .iconSet('notification', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-notification.svg', 24)
        .iconSet('social', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-social.svg', 24)
        .iconSet('toggle', 'https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-toggle.svg', 24)

        // Illustrated user icons used in the docs https://material.angularjs.org/latest/#/demo/material.components.gridList
        .iconSet('avatars', 'https://raw.githubusercontent.com/angular/material/master/docs/app/icons/avatar-icons.svg', 24)
        .defaultIconSet('https://raw.githubusercontent.com/google/material-design-icons/master/sprites/svg-sprite/svg-sprite-action.svg', 24);
});

app.factory('langService', ['$rootScope', function() {
    var langService = {};
    langService.ENGLISH = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,?-+!/\'\"[]{} ";
    langService.HEBREW = "אבגדהוזחטיכלמנסעפצקרשת.,?-+!/\'\"[]{} ףךץןם";
    langService.GENERAL = "0123456789.,?-+!/\'\"[]{} ";

    function stringIsInLang(str, charSet) {
        var index;
        for (index = str.length - 1; index >= 0; --index) {
            if (charSet.indexOf(str.substring(index, index + 1)) < 0) {
                return false;
            }
        }
        return true;
    }

    langService.isHebrew = function (str) {
        return stringIsInLang(str, langService.HEBREW);
    };

    langService.isEnglish = function (str) {
        return stringIsInLang(str, langService.ENGLISH);
    };

    langService.isGeneral = function (str) {
        return stringIsInLang(str, langService.GENERAL);
    };

    return langService;
}]);

app.factory('msgBus', ['$rootScope', function($rootScope) {
    var msgBus = {};
    msgBus.emitMsg = function(msg, data) {
        data = data || {};
        $rootScope.$emit(msg, data);
    };
    msgBus.onMsg = function(msg, func, scope) {
        var unbind = $rootScope.$on(msg, func);
        if (scope) {
            scope.$on('$destroy', unbind);
        }
    };
    return msgBus;
}]);

var audioFunction = function ($document) {
    var audioElement = $document[0].createElement('audio');
    return {
        audioElement: audioElement,

        play: function (filename) {
            if (filename) {
                audioElement.src = filename;
            }
            audioElement.play();
        },

        load: function (filename) {
            audioElement.src = filename;
            audioElement.load();
        }
    }
};
app.factory('audio1', audioFunction);
app.factory('audio2', audioFunction);

// for case where parent is viewing his child view and he want to get back to parent view
function returnToParentView(childId) {
    window.location.replace('/app/child/' + childId + '/returnToParentView');
}

var wrongAudio = new Audio('/sound/wrong-answer-2.mp3');
wrongAudio.load();
function playWrongSound() {
    wrongAudio.play();
}
