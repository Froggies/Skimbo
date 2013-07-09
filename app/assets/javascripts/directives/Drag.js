'use strict';

define(["app"], function(app) {

app.directive("drag", ["$rootScope", "$timeout", 
  function($rootScope, $timeout) { 
    
  var currentElement, firstOffsetX, firstOffsetY, originalElement;
  var containerId = "content";
  var container = angular.element(document.getElementById(containerId));
  var elements = []; 
  var listen = true; 

  function calculPosition(evt, useOffsetEvt) {
    var offsetX = firstOffsetX;
    var offsetY = firstOffsetY;
    if(useOffsetEvt == true) {
      offsetX = evt.offsetX || (evt.layerX - evt.currentTarget.offsetLeft);
      offsetY = evt.offsetY || (evt.layerY - evt.currentTarget.offsetTop);
      firstOffsetX = offsetX;
      firstOffsetY = offsetY;
    }
    return {
      x: (evt.pageX - offsetX), 
      y: (evt.pageY - offsetY)
    };
  }

  function isInside(c1, c2) {//c == {x:int, y:int, w:int, h:int}
    return c2.x > c1.x && c2.x+c2.w < c1.x+c1.w &&
      c2.y > c1.y && c2.y+c2.h < c1.y+c1.h;
  }

  function buildCarreMouseEvt(evt) {
    var offsetX = evt.offsetX || (evt.layerX - evt.target.offsetLeft);
    var offsetY = evt.offsetY || (evt.layerY - evt.target.offsetTop);
    return {
      x: (evt.pageX - offsetX),
      y: (evt.pageY - offsetY),
      w: 100,
      h: 100
    }
  }

  function buildCarreElmt(elmt) {
    return {
      x: elmt.offsetLeft,
      y: elmt.offsetTop,
      w: elmt.clientWidth,
      h: elmt.clientHeight
    }
  }

  function deleteElement(originalId) {
    for (var i = 0; i < elements.length; i++) {
      if(elements[i].originalId == originalId) {
        return elements.splice(i, 1);
      }
    }
  }

  container.bind('mousemove', function(evt) {
    $timeout(function() {
      if(currentElement) {
        var position = calculPosition(evt);
        currentElement[0].style.position = "absolute";
        currentElement[0].style.top = (position.y - 10)+"px";
        currentElement[0].style.left = position.x+"px";
        for(var i=0; i<elements.length && listen == true; i++) {
          var elm = document.getElementById(elements[i].originalId);
          if(elm) {//FIXME : remove and fix it !
            var inside = isInside(buildCarreElmt(elm), buildCarreMouseEvt(evt));
            if(inside && 
              currentElement.dragData.title != elements[i].dragData.title) {
              $rootScope.$broadcast("moveColumnEvent", currentElement.dragData, elements[i].dragData);
              listen = false;
              var oldWidth = currentElement[0].style.width;
              var oldHeight = currentElement[0].style.height;
              currentElement[0].style.width = elm.style.width;
              currentElement[0].style.height = elm.style.height;
              originalElement.style.width = elm.style.width;
              originalElement.style.height = elm.style.height;
              elm.style.width = oldWidth;
              elm.style.height = oldHeight;
              $timeout(function() {
                listen = true;
                var elm2 = document.getElementById(elements[i].originalId);
              }, 1000);
              return;
            }
          }
        }
      }
    }, 0);
  });
    
  container.bind('mouseup', function(evt) {
    if(currentElement != undefined) {         
      document.getElementById(containerId).removeChild(currentElement[0]);
      currentElement = undefined;
      angular.element(originalElement).removeClass("columnDrag");
      $rootScope.$broadcast("dropColumnEvent");
    }
  });
    
  return {
    restrict: 'A',
    link: function(scope, element, attrs)  {
      scope.dragStyle = attrs["dragstyle"];
      element.bind('mousedown', function(evt) {
        if(currentElement == undefined) {
          var div = document.createElement("div");
          originalElement = document.getElementById(attrs["dragElementId"]);
          div.innerHTML = originalElement.innerHTML;
          div.className = originalElement.className;
          document.getElementById(containerId).appendChild(div);
          currentElement = angular.element(div);
          currentElement.dragData = scope[attrs["drag"]];
          var position = calculPosition(evt, true);
          currentElement[0].style.position = "absolute";
          currentElement[0].style.top = (position.y - 10)+"px";
          currentElement[0].style.left = position.x+"px";
          currentElement[0].style.width = originalElement.style.width;
          currentElement[0].style.height = originalElement.style.height;
          angular.element(originalElement).addClass("columnDrag");
        }
      });
      attrs.$observe('dragElementId', function(val) {
        if(element.added === undefined) {
          element.added = true;
          element.originalId = val;
        } else {
          deleteElement(val);
        }
        var elmt = {
          dragData: scope[attrs["drag"]],
          originalId: val
        };
        elements.push(elmt);
      });
      element.bind("$destroy", function() {
        deleteElement(element.originalId);
      });
    }
  }

}]);

return app;
});