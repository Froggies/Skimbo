'use strict';

define(["app"], function(app) {

app.factory("Visibility", function() {

  var pageVisibility = (function(callWhenVisible, callWhenHidden) {
    var hidden, change, vis = {
            hidden: "visibilitychange",
            mozHidden: "mozvisibilitychange",
            webkitHidden: "webkitvisibilitychange",
            msHidden: "msvisibilitychange",
            oHidden: "ovisibilitychange"
        };             
    for (hidden in vis) {
        if (vis.hasOwnProperty(hidden) && hidden in document) {
            change = vis[hidden];
            break;
        }
    }
    if (change)
        document.addEventListener(change, onchange);
    else if (/*@cc_on!@*/false) // IE 9 and lower
        document.onfocusin = document.onfocusout = onchange
    else
        window.onfocus = window.onblur = onchange;

    function onchange (evt) {
        var body = document.body;
        evt = evt || window.event;

        if (evt.type == "focus" || evt.type == "focusin")
            callWhenVisible();
        else if (evt.type == "blur" || evt.type == "focusout")
            callWhenHidden();
        else        
            body.className = this[hidden] ? callWhenHidden() : callWhenVisible();
    }
  });

  var _isPageVisible = true;
  try {
    _isPageVisible = document.hasFocus();
  } catch(error) {
    _isPageVisible = true;
  }
  
  var nbNewMessages = 0;
  pageVisibility(switchPageVisible, switchPageInvisible);

  function switchPageVisible() {
    document.title = "Skimbo";
    nbNewMessages = 0;
    _isPageVisible = true;
  }

  function switchPageInvisible() {
    _isPageVisible = false;
    nbNewMessages = 0;
  }

  return {
    notifyNewMessage: function() {
      if (!_isPageVisible) {
        nbNewMessages += 1;
        document.title = "("+nbNewMessages+") Skimbo";
      }
    },
    isPageVisible: function() {
      return _isPageVisible;
    }
  }

});

return app;
});