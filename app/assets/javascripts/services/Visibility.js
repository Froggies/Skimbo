services.factory("Visibility", function() {

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