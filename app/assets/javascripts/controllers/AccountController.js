define(["app"], function(app) {

app.controller('AccountController', [
  "$scope", "DataCache", "Network", "ImagesUtils", "ArrayUtils",
  function($scope, $dataCache, $network, $imagesUtils, $arrayUtils) {

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

}]);

});