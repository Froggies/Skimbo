define(["app"], function(app) {

app.controller('AccountController', [
  "$scope", "DataCache", "Network", "ImagesUtils", "ArrayUtils", "$rootScope",
  function($scope, $dataCache, $network, $imagesUtils, $arrayUtils, $rootScope) {

    $scope.showContactUs = false;
    $scope.contactUsEmail = "";
    $scope.contactUsObject = "";
    $scope.contactUsMessage = "";

    $dataCache.on('userInfos', function(data) {
      $scope.userInfos = data.slice(0);

      $dataCache.on('tokenInvalid', function(tokensInvalid) {
        var badToken = {};
        for (var i = 0; i < tokensInvalid.length; i++) {
          var index = $arrayUtils.indexOf(
            $scope.userInfos, 
            {socialType: tokensInvalid[i].providerName}, 
            "socialType"
          );
          if(index < 0) {
            badToken = {};
            badToken.socialType = tokensInvalid[i].providerName;
            badToken.avatar = $imagesUtils.defaultImage();
            badToken.username = 'Invalid'; 
            $scope.userInfos.push(badToken);
          } else {
            $scope.userInfos[index].avatar = $imagesUtils.defaultImage();
            $scope.userInfos[index].username = 'Invalid'; 
          }
        }
      });
    });

    $scope.close = function() {
      $scope.$parent.hide();
    }

    $scope.selectImage = function(index) {
      var temp = $scope.userInfos[0];
      $scope.userInfos[0] = $scope.userInfos[index];
      $scope.userInfos[index] = temp;
      $dataCache.add('userInfos', $scope.userInfos);
    }

    $scope.deleteProvider = function(providerName) {
      $network.send({cmd: 'deleteProvider', body: {provider: providerName}});
      for (var i = 0; i < $scope.userInfos.length; i++) {
        if($scope.userInfos[i].socialType == providerName) {
          $scope.userInfos.splice(i,1);
          break;
        }
      }
    }

    $scope.pauseProvider = function(providerName) {
      $network.send({cmd: 'pauseProvider', body: {provider: providerName}});
    }

    $scope.showHelp = function() {
      $rootScope.$broadcast('glassShowView', 'help', function() {
        $rootScope.$broadcast('goOnClick', 'helpIdAccount');
      });
    }

    $scope.send = function() {
      $network.send({
        cmd: 'sendEmail', 
        body: {
          email: $scope.contactUsEmail,
          object: $scope.contactUsObject,
          message: $scope.contactUsMessage
        }
      });
      $scope.contactUsEmail = "";
      $scope.contactUsObject = "";
      $scope.contactUsMessage = "";
      $scope.showContactUs = false;
    }

    $scope.selectFrLang = function() {
      $rootScope.currentLanguage = "fr";
      //$rootScope.$apply();
    }

    $scope.selectGbLang = function() {
      $rootScope.currentLanguage = "en";
      //$rootScope.$apply();
    }


}]);

});