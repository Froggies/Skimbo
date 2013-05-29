'use strict';

define(["app"], function(app) {

app.directive("drag2", ["$rootScope", function($rootScope) { 
    
  var currentElement, firstOffsetX, firstOffsetY, originalElement;
  var containerId = "content";
  var container = angular.element(document.getElementById(containerId));
  var elements = [];  

  function calculPosition(evt, useOffsetEvt) {
    var offsetX = firstOffsetX;
    var offsetY = firstOffsetY;
    if(useOffsetEvt == true) {
      offsetX = evt.offsetX;
      offsetY = evt.offsetY;
      firstOffsetX = evt.offsetX;
      firstOffsetY = evt.offsetY;
    }
    return {
      x: (evt.pageX - offsetX), 
      y: (evt.pageY - offsetY)
    };
  }

  container.bind('mousemove', function(evt) {
    if(currentElement) {
        var elem = currentElement;
        var position = calculPosition(evt);
        elem[0].style.position = "absolute";
        elem[0].style.top = position.y+"px";
        elem[0].style.left = position.x+"px";
        for(var i=0; i<elements.length; i++) {
          var nelem = elements[i][0];
          console.log(nelem, evt);
          if(nelem.offsetTop - nelem.offsetHeight > evt.pageY && 
            nelem.offsetTop + nelem.height < evt.pageY && 
            nelem.offsetLeft - nelem.offsetWidth > evt.pageX && 
            nelem.offsetLeft + nelem.width < evt.pageX &&
            originalElement.dragData.title != elements[i].dragData.title) {
            $rootScope.$broadcast("dropEvent", originalElement.dragData, elements[i].dragData);
            angular.element(nelem).addClass("columnDrop");
            return;
          } else {
            angular.element(nelem).removeClass("columnDrop");
          }
      }
    }
  });
    
  container.bind('mouseup', function(evt) {
    if(currentElement != undefined) {         
      document.getElementById(containerId).removeChild(currentElement[0]);
      currentElement = undefined;
      originalElement.removeClass("columnDrag");
      for(var i=0; i<elements.length; i++) {
        elements[i].removeClass("columnDrop");
      }
    }
  });
    
  return {
    restrict: 'A',
    link: function(scope, element, attrs)  {
      scope.dragData = scope[attrs["drag2"]];
      scope.dragStyle = attrs["dragstyle"];
      element.dragData = scope[attrs["drag2"]];
      element.bind('mousedown', function(evt) {
        if(currentElement == undefined) {
          var div = document.createElement("div");
          div.innerHTML = element[0].innerHTML;
          div.className = element[0].className;
          document.getElementById(containerId).appendChild(div);
          currentElement = angular.element(div);
          var position = calculPosition(evt, true);
          currentElement[0].style.position = "absolute";
          currentElement[0].style.top = position.y+"px";
          currentElement[0].style.left = position.x+"px";
          currentElement[0].style.width = element[0].style.width;
          currentElement[0].style.height = element[0].style.height;
          originalElement = element;
          element.addClass("columnDrag");
          console.log(evt, originalElement);
        }
      });
      elements.push(element);
    }
  }

}]);

return app;
});