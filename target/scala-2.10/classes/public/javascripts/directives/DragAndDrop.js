'use strict';

define(["app"], function(app) {

  app.directive("drag", [function() {
  
    function dragStart(evt, element, dragStyle) {
      element.addClass(dragStyle);
      evt.dataTransfer.setData("id", evt.target.id);
      evt.dataTransfer.effectAllowed = 'move';
    };
    function dragEnd(evt, element, dragStyle) {
      element.removeClass(dragStyle);
    };
    
    return {
      restrict: 'A',
      link: function(scope, element, attrs)  {
        attrs.$set('draggable', 'true');
        element.bind('dragstart', function(evt) {
          dragStart(evt, element, attrs['dragstyle']);
        });
        element.bind('dragend', function(evt) {
          dragEnd(evt, element, attrs['dragstyle']);
        });
      }
    }
  }]);

  app.directive("drop", ["Network", function($network) {
    
    function dragEnter(evt, element, dropStyle) {
      evt.preventDefault();
      element.addClass(dropStyle);
    };
    function dragLeave(evt, element, dropStyle) {
      element.removeClass(dropStyle);
    };
    function dragOver(evt) {
      evt.preventDefault();
    };
    function drop(evt, angScope, element, dropStyle) {
      evt.preventDefault();
      element.removeClass(dropStyle);
    
      var fromId = evt.dataTransfer.getData("id");
      fromId = fromId.split("_")[1];
      var toId = angular.element(element).attr('id');
      toId = toId.split("_")[1];
      if(fromId != toId) {
        var scope = angScope.$parent;
        scope.$apply(function() {
          var temp = scope.columns[fromId];
          scope.columns[fromId] = scope.columns[toId];
          scope.columns[fromId].index = toId;
          scope.columns[toId] = temp;
          scope.columns[toId].index = fromId;
          scope.$eval(scope.columns);
          var modColumnsOrder = {"cmd":"modColumnsOrder", "body":{}};
          modColumnsOrder.body.columns = [];
          for (var i = 0; i < scope.columns.length; i++) {
            modColumnsOrder.body.columns.push(scope.columns[i].title);
          };
          $network.send(modColumnsOrder);
        });
      }
    };
    
    return {
      restrict: 'A',
      link: function(scope, element, attrs)  {
        element.bind('dragenter', function(evt) {
          dragEnter(evt, element, attrs['dropstyle']);
        });
        element.bind('dragleave', function(evt) {
          dragLeave(evt, element, attrs['dropstyle']);
        });
        element.bind('dragover', dragOver);
        element.bind('drop', function(evt) {
          drop(evt, scope, element, attrs['dropstyle']);
        });
      }
    }
  }]);

return app;
});