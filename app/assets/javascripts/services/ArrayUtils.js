'use strict';

define(["app"], function(app) {

app.factory("ArrayUtils", function() {

  return {

    exist: function(array, object, field) {
      return this.indexOf(array, object, field) > -1;
    },

    indexOf: function(array, object, field) {
      var index = -1;
      if(array !== undefined) {
        if(field  !== undefined) {
          return this.indexOfWith(array, object, function(inArray, data) {
            return inArray[field] == data[field];
          });
        } else {
          return this.indexOfWith(array, object, function(inArray, data) {
            return inArray == data;
          });
        }
      }
      return index;
    },

    existWith: function(array, object, cond) {
      return this.indexOfWith(array, object, cond) > -1;
    },

    indexOfWith: function(array, object, cond) {
      var index = -1;
      if(array !== undefined && cond !== undefined) {
        for (var i = 0; i < array.length; i++) {
          if (cond(array[i], object) == true) {
            return i;
          }
        }
      }
      return index;
    },

    sortMsg: function(sortMe, newMsg) {
      var inserted = false;
      newMsg.dateAgo = moment(moment(Number(newMsg.createdAt)), "YYYYMMDD").fromNow();
      for(var i=0; i<sortMe.length; ++i ) {
        if(sortMe[i].createdAt < newMsg.createdAt && inserted == false) {
          sortMe.splice(i, 0, newMsg);
          inserted = true;
        }
        //refresh time
        sortMe[i].dateAgo = moment(moment(Number(sortMe[i].createdAt)), "YYYYMMDD").fromNow();
      }
      if(inserted == false) {
        sortMe.push(newMsg);
      }
    }

  };

});

return app;
});