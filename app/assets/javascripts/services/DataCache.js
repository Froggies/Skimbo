'use strict';

define(["app"], function(app) {

app.factory("DataCache", [
  "$rootScope", "ImagesUtils", "ArrayUtils", "Network",
  function($rootScope, $imagesUtils, $arrayUtils, $network) {

  var _cache = {};
  var _callback = {};

  function add(key, data) {
    _cache[key] = data;
    fireEvent(key);
  }

  function fireEvent(key) {
    if(_callback[key] !== undefined) {
      for (var i = 0; i < _callback[key].length; i++) {
        _callback[key][i](_cache[key]);
      };
    }
  }

  $rootScope.$on('userInfos', function(evt, data) {
    if(_cache.userInfos == undefined) {
      _cache.userInfos = [];
      _cache.userInfos.push(data);
    } else {
      var index = $arrayUtils.indexOf(_cache.userInfos, data, "socialType");
      if(index > -1) {
        _cache.userInfos[index] = data;
      } else {
        if($imagesUtils.isDefaultImage(_cache.userInfos[0].avatar)) {
          _cache.userInfos.splice(0, 0, data);
        } else {
          _cache.userInfos.push(data);
        }
      }
    }
    fireEvent('userInfos');
  });

  $rootScope.$on('deleteProvider', function(evt, data) {
    data.socialType = data.provider;
    var index = $arrayUtils.indexOf(_cache.userInfos, data, "socialType");
    if(index > -1) {
      _cache.userInfos.splice(index, 1);
    }

    data.providerName = data.provider;
    var index = $arrayUtils.indexOf(_cache.tokenInvalid, data, "providerName");
    if(index > -1) {
      _cache.tokenInvalid.splice(index, 1);
    }

    fireEvent('userInfos');
    fireEvent('tokenInvalid');
  });

  $rootScope.$on('tokenInvalid', function(evt, data) {
    if(_cache.tokenInvalid == undefined) {
      _cache.tokenInvalid = [];
      _cache.tokenInvalid.push(data);
    } else {
      var index = $arrayUtils.indexOf(_cache.tokenInvalid, data, "providerName");
      if(index > -1) {
        _cache.tokenInvalid[index] = data;
      } else {
        _cache.tokenInvalid.push(data);
      }
    }
    fireEvent('tokenInvalid');
  });

  $rootScope.$on('allColumns', function(evt, columns) {
    add('allColumns', columns);
  });

  $rootScope.$on('addColumn', function(evt, column) {
    _cache.allColumns.push(column);
    fireEvent('allColumns');
  });

  $rootScope.$on('modColumn', function(evt, column) {
    var index = $arrayUtils.indexOf(_cache.allColumns, column, "title");
    if(index > -1) {
      _cache.allColumns[index] = column.column;
      fireEvent('allColumns');
    }
  });

  $rootScope.$on('delColumn', function(evt, column) {
    var index = $arrayUtils.indexOf(_cache.allColumns, column, "title");
    if(index > -1) {
      _cache.allColumns.splice(index, 1);
      fireEvent('allColumns');
    }
  });

  $rootScope.$on('allUnifiedRequests', function(evt, providers) {
    add('allUnifiedRequests', providers);
  });

  return {
    get: function(key, callback) {
      return _cache[key];
    },
    add: function(key, data) {
      add(key, data);
    },
    on: function(key, callback) {
      _callback[key] = _callback[key] || [];
      _callback[key].push(callback);
      if(_cache[key] !== undefined) {
        callback(_cache[key]);
      } else if(key == "allUnifiedRequests") {
        $network.send({cmd:"allUnifiedRequests"});
      }
    }
  }

}]);

return app;
});