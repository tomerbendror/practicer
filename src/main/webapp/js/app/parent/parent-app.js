var parentApp = angular.module('parentApp', [
    'ngRoute', 'ngTagsInput', 'ui.bootstrap', 'angular-sortable-view', 'uiSwitch', 'angular-cache', 'infinite-scroll', 'ngAnimate'
]);

parentApp.run(['$route', '$rootScope', '$location', function ($route, $rootScope, $location) {
    $rootScope.formatDate = function (date, format) {
        return formatDate(date, format);
    };

    $rootScope.isMobileOrTablet = isMobileOrTablet();

    var original = $location.path;
    $location.path = function (path, reload) {
        if (reload === false) {
            var lastRoute = $route.current;
            var un = $rootScope.$on('$locationChangeSuccess', function () {
                $route.current = lastRoute;
                un();
            });
        }
        return original.apply($location, [path]);
    };
}]);

parentApp.config(function ($httpProvider, tagsInputConfigProvider) {
    tagsInputConfigProvider.setActiveInterpolation('tagsInput', { placeholder: true });

    $httpProvider.interceptors.push(function($q) {
        return {
            'responseError': function(rejection) {
                if(rejection.status == 901 || rejection.status == 401) {
                    window.location = "/user/login";
                }
                return $q.reject(rejection);
            }
        };
    });
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
});

parentApp.factory('msgBus', ['$rootScope', function($rootScope) {
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


parentApp.factory('langService', ['$rootScope', function() {
    var langService = {};
    langService.ENGLISH = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,:/?-+!/\'\"[]{} ";
    langService.HEBREW = "אבגדהוזחטיכלמנסעפצקרשת.,?-+!/\'\"[]{} ףךץןם0123456789:/";
    langService.UNIVERSAL = "0123456789.,?:*xX-+!/\'\"[]{} ";

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

    langService.isUniversal = function (str) {
        return stringIsInLang(str, langService.UNIVERSAL);
    };

    return langService;
}]);

parentApp.factory( 'contextService', function() {
    var userId = $.cookie("userId");
    return {
        getUserId: function() { return userId; }
    };
});

parentApp.factory( 'yesNoModal', function($modal) {
    var yesNoModal = {};

    yesNoModal.open = function(title, message, callback, yesText, noText) {
        if (!yesText) {
            yesText = 'כן';
        }
        if (!noText && noText != '') {
            noText = 'לא';
        }
        var modalInstance = $modal.open({
            templateUrl: '/js/app/parent/yes-no-modal/yesNoModal.html',
            controller: function modalController ($scope, $modalInstance, title, message, yesText, noText) {
                $scope.title = title;
                $scope.message = message;
                $scope.yesText = yesText;
                $scope.noText = noText;

                $scope.done = function () {
                    $modalInstance.close('yes');
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('no');
                };
            },
            resolve: {
                'title': function() { return title; },
                'message': function() { return message; },
                'yesText': function() { return yesText; },
                'noText': function() { return noText; }
            }
        });
        modalInstance.result.then(callback, callback);
    };

    return yesNoModal;
});

parentApp.factory( 'practiceOperationService', function($q, $modal, $location, parentDataService, contextService) {
    var practiceOperationService = {};

    practiceOperationService.deletePractice = function(practice) {
        var deferred = $q.defer();
        if (practice.id > 0) {
            bootbox.confirm("האם להסיר את התרגיל '"+ practice.name + "' לצמיתות?", function(userAnswer) {//todo i18n
                if (userAnswer) {
                    parentDataService.deletePractice(practice.id)
                        .then(function (deletedPracticeId) {
                            deferred.resolve(deletedPracticeId);
                            toastr.success('התרגיל נמחק בהצלחה');//todo i18n
                        }, function (errorMsg) {
                            deferred.reject(errorMsg);
                        });
                } else {
                    deferred.reject('canceled');
                }
            });
        }
        return deferred.promise;
    };

    practiceOperationService.duplicatePractice = function(practice) {
        if (practice.id < 1) {
            toastr.error('יש לשמור את התרגיל תחילה');
            return;
        }

        var modalInstance = $modal.open({
            templateUrl: '/js/app/parent/duplicate-practice-dialog/duplicatePracticeDialog.html',
            controller: 'duplicatePracticeDialogController',
            resolve: {
                'srcPractice': function() { return practice; }
            }
        });
        alInstance.result.then(function (updatedPractice) {
            $location.path('/practice/' + updatedPractice.id);
            toastr.success('התרגיל נוצר בהצלחה');
        });
    };

    practiceOperationService.copyQuestionsToExistPractice = function(practice) {
        var practiceQuestionsType = practice.questionsType;
        var possibleTargets = [];
        parentDataService.getPracticesSummary()
            .then(function (practices) {
                for (var i=0; i<practices.length; i++) {
                    var currentPractice = practices[i];
                    if (currentPractice.id != practice.id && currentPractice.creatorId == contextService.getUserId() &&
                        currentPractice.questionsType == practiceQuestionsType) {
                        possibleTargets.push(currentPractice);
                    }
                }

                if (possibleTargets.length == 0) {
                    toastr.warning('לא קיים תרגיל אליו ניתן להעתיק את השאלות');
                    return;
                }
                var modalInstance = $modal.open({
                    templateUrl: '/js/app/parent/copy-questions-to-exist-practice-dialog/copyQuestionsToExistPractice.html',
                    controller: 'copyQuestionToExistPracticeDialogController',
                    resolve: {
                        'possibleTargets': function() { return possibleTargets; },
                        'practice': function() { return practice; }
                    }
                });
                modalInstance.result.then(function (updatedPractice) {
                    $location.path('/practice/' + updatedPractice.id);
                    toastr.success('השאלות הועתקו לתרגיל בהצלחה');
                });
            });
    };
    return practiceOperationService;
});

parentApp.config(['$routeProvider', '$locationProvider',
    function($routeProvider, $locationProvider) {
        $routeProvider.
            when('/childs', {
                templateUrl: '/html/app/parent/parent-childs.html',
                controller: parentChildsCtrl
            }).
            when('/', {      // todo - need to find a nicer way to handle two url to same controller
                templateUrl: '/html/app/parent/parent-childs.html',
                controller: parentChildsCtrl
            }).
            when('/groups', {
                templateUrl: '/html/app/parent/parent-groups.html',
                controller: parentGroupsCtrl
            }).
            when('/tasks', {
                templateUrl: '/html/app/parent/parent-tasks.html',
                controller: parentTasksCtrl
            }).
            when('/exercises', {
                templateUrl: '/html/app/parent/parent-exercises.html',
                controller: parentExercisesCtrl
            }).
            when('/exercisesByGroups', {
                templateUrl: '/js/app/parent/exercises-by-groups/parent-exercises-by-groups.html',
                controller: parentExercisesByGroupsCtrl
            }).
            when('/exercisesByChild', {
                templateUrl: '/js/app/parent/exercises-by-child/parent-exercises-by-child.html',
                controller: parentExercisesByChildCtrl
            }).
            when('/profile', {
                templateUrl: '/js/app/parent/parent-profile/parent-profile.html',
                controller: parentProfileCtrl
            }).
            when('/practice/:param', {  // the param may be the practice id for update scenario or questionType for new practice scenario
                templateUrl: '/html/app/parent/singlePractice.html',
                controller: singlePractice
            }).
            otherwise({
                redirectTo: '/'
            });

        // use the HTML5 History API
        $locationProvider.html5Mode(true);
    }])
;

parentApp.controller('menuCtrl', ['$scope', 'msgBus', '$http', 'contextService', function($scope, msgBus, $http) {
    $scope.refreshParentData = function() {
        $http.get('/parent/summary').then(function (result) {
            var parentSummary = result.data;
            $scope.childCount = parentSummary.childCount;
            $scope.groupsCount = parentSummary.groupsCount;
            $scope.practicesCount = parentSummary.practicesCount;
            $scope.profileImageUrl = parentSummary.profileImageUrl;
            $scope.parentDisplayName = parentSummary.displayName;
        });
    };

    msgBus.onMsg(PARENT_PROFILE_CHANGE_EVENT, function(event, updatedProfile) {
        $scope.parentDisplayName = updatedProfile.displayName;
    }, $scope);

    msgBus.onMsg('childCreated', function() {
        $scope.refreshParentData();
    }, $scope);

    msgBus.onMsg('childRemoved', function() {
        $scope.refreshParentData();
    }, $scope);

    msgBus.onMsg('groupCreated', function() {
        $scope.refreshParentData();
    }, $scope);

    msgBus.onMsg('groupRemoved', function() {
        $scope.refreshParentData();
    }, $scope);

    msgBus.onMsg(PRACTICE_CREATED_EVENT, function() {
        $scope.refreshParentData();
    }, $scope);

    msgBus.onMsg(PRACTICE_DELETED_EVENT, function() {
        $scope.refreshParentData();
    }, $scope);

    $scope.refreshParentData();
}]);

parentApp.directive('audioBtn', function() {
    return {
        restrict: 'AEC',
        template:   '<div class="audio-btn-div">' +
        '<audio class="player"></audio>' +
        '<button class="play btn btn-primary"><i class="fa fa-volume-up"></i></button>' +
        '</div>',
        link: function(scope, element, attr) {
            if (scope.question.id != -1) {
                var player = element.find('.player')[0];
                if (attr.async != "true") {
                    player.src = attr.src;
                } else {
                    $(player).attr('async-src', attr.src);
                }
                element.find('.play').on('click', function () {
                    player.play();
                });
            }
        }
    };
});

parentApp.directive('moreBtn', function() {
    return {
        restrict: 'AEC',
        template:   '',
        link: function(scope, element, attr) {
            var elmnt = $(element);
            elmnt.css('text-indent', '-5000px');
            elmnt.css('width', '27px');
            elmnt.css('height', '32px');
            elmnt.css('background-repeat', 'no-repeat');
            elmnt.css('background-size', '21px 21px');
            elmnt.css('background-image', "url('/images/three-dots.png')");
            elmnt.css('background-position', '2px 4px');
        }
    };
});

parentApp.directive('busyButton', function() {
    return {
        scope: {},
        restrict: 'E',
        template:   '<button class="{{className}}"></button>',
        link: function(scope, element, attr) {
            scope.element = element;
            scope.caption = attr.caption;
            scope.currentCaption = attr.caption;
            scope.busyCaption = attr.busyCaption;
            scope.showLoading = attr.showLoading;
            scope.className = element.attr('class');
            element.removeAttr('class');
            scope.btn = element.find('button');

            attr.$observe('showLoading', function(showLoading) {
                scope.showLoading = attr.showLoading;
                scope.currentCaption = (showLoading == 'true') ? attr.busyCaption : attr.caption;
                validateCaptionAndDisable();
            });

            attr.$observe('caption', function(caption) {
                scope.caption = caption;
                scope.currentCaption = (scope.showLoading == 'true') ? attr.busyCaption : attr.caption;
                validateCaptionAndDisable();
            });

            function validateCaptionAndDisable() {
                if (scope.showLoading == 'true') {
                    scope.btn.attr('disabled','disabled');
                    scope.btn.html('<i class="fa fa-spin fa-spinner"></i> ' + scope.currentCaption);
                } else {
                    scope.btn.removeAttr('disabled');
                    scope.btn.html(scope.currentCaption);
                }
            }
        }
    };
});

parentApp.directive('focusMe',
    ['$timeout',
        function ($timeout) {
            return {
                link: {
                    pre: function preLink(scope, element, attr) {
                    },
                    post: function postLink(scope, element, attr) {
                        $timeout(function () {
                            element[0].focus();
                        }, 0);


                    }
                }
            }
        }]);

parentApp.directive('childInfoPanel', childInfoPanel);
parentApp.directive('groupInfoPanel', groupInfoPanel);
parentApp.directive('practiceInfoPanel', practiceInfoPanel);
parentApp.directive('taskInfoPanel', taskInfoPanel);
parentApp.directive('singleQuestionPanel', singleQuestionPanel);

//Finds y value of given object
function findPos(obj) {
    var curtop = 0;
    if (obj.offsetParent) {
        do {
            curtop += obj.offsetTop;
        } while (obj = obj.offsetParent);
        return curtop;
    }
}

function getPracticeTypeStr(questionsType) {
    if (questionsType == 'MATH') {
        return 'תרגול חשבון';//todo i18n
    } else if (questionsType == 'DICTATION') {
        return 'הכתבה';//todo i18n
    } else if (questionsType == 'TRANSLATION') {
        return 'תרגום מילים';//todo i18n
    }
}

function trimToLen(str, maxLen) {
    if (str.length <= maxLen) {
        return str;
    }
    var trimmed = str.substr(0, maxLen);
    return trimmed.substr(0, trimmed.lastIndexOf(' ')) + '…';
}

function isPositiveNumeric(input) {
    return (input - 0) == input && (''+input).trim().length > 0 && eval(input) > 2;
}

function isPracticeType(str) {
    return str == 'TRANSLATION' || str == 'DICTATION'  || str == 'MATH' ;
}

function deepCopyCollection(collection) {
    if ($.isArray(collection)) {
        var copy = [];
        for (var i = 0; i < collection.length; i++) {
            copy.push(angular.copy(collection[i]));
        }
        return copy;
    } else {
        return JSON.parse(JSON.stringify(collection));
    }
}

Array.prototype.clear = function() {
    this.length = 0;
};

Array.prototype.pushArray = function(arr) {
    this.push.apply(this, arr);
};

// todo move to common.js when support the child app
function formatDate(dateObj, format) {
    if (!format) {
        format = 3;
    }
    dateObj = new Date(dateObj);
    var monthNames = [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ];
    var curr_date = dateObj.getDate();
    var curr_month = dateObj.getMonth();
    curr_month = curr_month + 1;
    var curr_year = dateObj.getFullYear();
    var curr_year_ = dateObj.getFullYear() - 2000;
    var curr_min = dateObj.getMinutes();
    var curr_hr = dateObj.getHours();
    var curr_sc = dateObj.getSeconds();
    if (curr_month.toString().length == 1)
        curr_month = '0' + curr_month;
    if (curr_date.toString().length == 1)
        curr_date = '0' + curr_date;
    if (curr_hr.toString().length == 1)
        curr_hr = '0' + curr_hr;
    if (curr_min.toString().length == 1)
        curr_min = '0' + curr_min;

    if (format == 1) {          //dd-mm-yyyy
        return curr_date + "-" + curr_month + "-" + curr_year;
    } else if (format == 2) {     //yyyy-mm-dd
        return curr_year + "-" + curr_month + "-" + curr_date;
    } else if (format == 3) {     //dd/mm/yyyy
        return curr_date + "/" + curr_month + "/" + curr_year;
    } else if (format == 4) {     // MM/dd/yyyy HH:mm:ss
        return curr_month + "/" + curr_date + "/" + curr_year + " " + curr_hr + ":" + curr_min + ":" + curr_sc;
    } else if (format == 5) {     // dd/MM/yy
        return curr_date + "/" + curr_month + "/" + curr_year_;
    } else if (format == 6) {     // dd/MM/yy
        return curr_date + "/" + curr_month + "/" + curr_year;
    }
}

function setActiveMenu(activeMenuSelector) {
    $('body.wrapkit-sidebar-right .sidebar .sidebar-nav .nav>li').removeClass('active');    // first level items
    $('body.wrapkit-sidebar-right .sidebar .sidebar-nav .nav-item').removeClass('active'); // second level items
    $(activeMenuSelector).closest("li").toggleClass( "active" );
}

var TOP_HEADER_HEIGHT = 55;
var PARENT_PROFILE_CHANGE_EVENT = 'PARENT_PROFILE_CHANGE_EVENT';
var QUESTIONS_TTS_URL_UPDATE_EVENT = 'QUESTIONS_TTS_URL_UPDATE_EVENT';
var PRACTICE_DELETED_EVENT = 'PRACTICE_DELETED_EVENT';
var PRACTICE_CREATED_EVENT = 'PRACTICE_CREATED_EVENT';

var PARENT_SAW_WELCOME_MSG_COOKIE = 'PARENT_SAW_WELCOME_MSG_COOKIE';