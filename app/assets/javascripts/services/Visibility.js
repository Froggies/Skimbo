services.factory("Visibility", function() {

  var isPageVisible = true;
  try {
    isPageVisible = document.hasFocus();
  } catch(error) {
    isPageVisible = true;
  }
  
  var nbNewMessages = 0;
  pageVisibility(switchPageVisible, switchPageInvisible);

  function switchPageVisible() {
    document.title = "Skimbo";
    nbNewMessages = 0;
    isPageVisible = true;
  }

  function switchPageInvisible() {
    isPageVisible = false;
    nbNewMessages = 0;
  }

  return {
    notifyNewMessage: function() {
      if (!isPageVisible) {
        nbNewMessages += 1;
        document.title = "("+nbNewMessages+") Skimbo";
      }
    }
  }

});