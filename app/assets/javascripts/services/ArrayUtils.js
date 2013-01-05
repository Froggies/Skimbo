services.factory("ArrayUtils", function() {

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

    sortMsg: function(sortMe) {
      for(var i=0, j, tmp; i<sortMe.length; ++i ) {
        tmp = sortMe[i];
        tmp.dateAgo = moment(moment(Number(tmp.createdAt)), "YYYYMMDD").fromNow();
        for(j=i-1; j>=0 && sortMe[j].createdAt < tmp.createdAt; --j) {
          sortMe[j+1] = sortMe[j];
        }
        sortMe[j+1] = tmp;
      }
    }

  };

});