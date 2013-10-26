(function () {

  'use strict';

  angular.module('publicApp').factory("Visibility", function() {

    var nbNewMessages = 0;

    return {
      notifyNewMessage: function() {
        nbNewMessages += 1;
        document.title = "("+nbNewMessages+") Skimbo";
      },
      notifyMessageRead: function() {
        if(nbNewMessages > 0) {
          nbNewMessages -= 1;
        }
        if(nbNewMessages == 0) {
          document.title = "Skimbo";
        } else {
          document.title = "("+nbNewMessages+") Skimbo";
        }
      }
    }

  });

})();