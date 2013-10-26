(function () {

  'use strict';

  angular.module('publicApp').factory("PopupProvider", ["Network", "$rootScope", function($network, $rootScope) {

    return {
      openPopup: function(socialNetwork, optionalCallback) {
        var width = 500;
        var height = 500;

        switch(socialNetwork.name) {
          case "github":
            width = 960;
            height = 430;
          break;
          case "facebook":
           width = 640;
            height = 372;   
          break;
          case "viadeo":
            width = 570;
            height = 315;
          break;
          case "trello":
            width = 572;
            height = 610;
          break;
          case "twitter":
            width = 600;
            height = 500;
          break;
        }

        if (window.innerWidth) {
          var left = (window.innerWidth-width)/2;
          var top = (window.innerHeight-height)/2;
        } else {
           var left = (document.body.clientWidth-width)/2;
           var top = (document.body.clientHeight-height)/2;
        }
        
        var newwindow = window.open("/auth/"+socialNetwork.name, 'Connection', 'height='+height+', width='+width+', left='+left+', top='+top);
        window.callMeToRefresh = function() {
          $network.send({cmd:"allUnifiedRequests"});
          if(optionalCallback !== undefined) {
            optionalCallback();
          }
        }

        if (newwindow !== undefined && window.focus) {
          newwindow.focus();
        }

        $rootScope.$broadcast('loading', {loading: true, translationCode: 'GET_TOKEN_PROGRESS'});

        return false;
      }
    }

  }]);

})();