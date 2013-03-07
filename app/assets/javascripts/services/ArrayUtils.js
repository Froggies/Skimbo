'use strict';

define(["app"], function(app) {

app.factory("ArrayUtils", function() {

  var insertedMsgInProgress = false;
  var msgToBeInserted = [];

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
      if(insertedMsgInProgress == false) {
        insertedMsgInProgress = true;
        var index = -1;
        var isOldMsg = false;
        newMsg.dateAgo = moment(moment(Number(newMsg.createdAt)), "YYYYMMDD").fromNow();
        for(var i=0, len=sortMe.length; i<len; ++i ) {
          if(sortMe[i].createdAt < newMsg.createdAt) {
            index = i;
          }
          if(sortMe[i].idProvider == newMsg.idProvider || 
            (sortMe[i].authorName == newMsg.authorName && sortMe[i].message == newMsg.message)) {
            isOldMsg = true;
            //update old msg
            if(newMsg.shared !== undefined) {
              sortMe[i].shared = newMsg.shared;
            }
          }
          //refresh time
          sortMe[i].dateAgo = moment(moment(Number(sortMe[i].createdAt)), "YYYYMMDD").fromNow();
        }
        //insert data
        if(index > -1 && isOldMsg == false) {
          sortMe.splice(index, 0, newMsg);
        } else if(isOldMsg == false) {
          sortMe.push(newMsg);
        }
        //recursion powa
        if(msgToBeInserted.length == 0) {
          insertedMsgInProgress = false;
        } else {
          var toBeInserted = msgToBeInserted.shift();
          this.sortMsg(toBeInserted.tab, toBeInserted.msg);
        }
      } else {
        msgToBeInserted.push({
          tab: sortMe,
          msg: newMsg
        })
      }
    }

  };

});

return app;
});