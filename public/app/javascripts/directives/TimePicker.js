'use strict';

define(["app"], function(app) {

  app.directive('timePicker', [function() {

    var defaultCssStyle = [
      '-webkit-transform: rotate(', '0', 'deg);',
      '-moz-transform: rotate(', '0', 'deg);',
      '-o-transform: rotate(', '0', 'deg);',
      'transform: rotate(', '0', 'deg);'
    ];

    function getDefaultCssStyle(degree) {
      var tab = defaultCssStyle.slice(0);
      tab[1] = degree;
      tab[4] = degree;
      tab[7] = degree;
      tab[10] = degree;
      return tab.join('');
    }

    return {
      restrict: 'E',
      replace: true,
      transclude: true,
      scope: {},
      template: [
        '<div class="time-picker-global-container">',
          '<div class="time-picker-horloge-container">',
            '<div class="time-picker-horloge-hour" style="{{styleHorlogeHour}}"></div>',
            '<div class="time-picker-horloge-minute" style="{{styleHorlogeMinute}}"></div>',
            '<div class="time-picker-horloge-center"></div>',
          '</div>',
          '<div class="time-picker-selector-container">',
            '<div class="time-picker-selector-hour">',
              '<div class="time-picker-up" ng-click="upHour()">+</div>',
              '<div>{{hour}}</div>',
              '<div class="time-picker-down" ng-click="downHour()">-</div>',
            '</div>',
            '<div class="time-picker-selector-minute">',
              '<div class="time-picker-up" ng-click="upMinute()">+</div>',
              '<div>{{minute}}</div>',
              '<div class="time-picker-down" ng-click="downMinute()">-</div>',
            '</div>',
          '</div>',
        '</div>'
      ].join(''),
      link: function($scope, element, attrs) {
        
        var selectTimeCallback = $scope.$parent[attrs["selectTime"].split("(")[0]];

        $scope.hour = 0;
        $scope.styleHorlogeHour = getDefaultCssStyle(0);
        $scope.styleHorlogeMinute = getDefaultCssStyle(180);
        $scope.minute = 30;

        $scope.upHour = function() {
          $scope.hour++;
          if($scope.hour >= 24) {
            $scope.hour = 0;
          }
          updateHorlogeHour();
        }

        $scope.downHour = function() {
          $scope.hour--;
          if($scope.hour <= -1) {
            $scope.hour = 23;
          }
          updateHorlogeHour();
        }

        $scope.upMinute = function() {
          $scope.minute += 5;
          if($scope.minute >= 60) {
            $scope.minute = 0;
          }
          updateHorlogeMinute();
        }

        $scope.downMinute = function() {
          $scope.minute -= 5;
          if($scope.minute <= -1) {
            $scope.minute = 55;
          }
          updateHorlogeMinute();
        }

        function updateHorlogeHour() {
          var degree = $scope.hour * 360 / 12;
          $scope.styleHorlogeHour = getDefaultCssStyle(degree);
          fireCallback();
        }

        function updateHorlogeMinute() {
          var degree = $scope.minute * 360 / 60;
          $scope.styleHorlogeMinute = getDefaultCssStyle(degree);
          fireCallback();
        }

        function fireCallback() {
          if(selectTimeCallback !== undefined) {
            selectTimeCallback($scope.hour, $scope.minute);
          }
        }

      }
    }

  }]);
});