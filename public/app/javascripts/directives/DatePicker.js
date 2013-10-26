(function () {

  'use strict';

  angular.module('publicApp').directive('datePicker', [function() {

    return {
      restrict: 'E',
      replace: true,
      transclude: true,
      scope: {},
      template: [
        '<div class="date-picker-global-container">',
          '<div class="date-picker-controls-header">',
            '<a class="date-picker-prev" ng-click="prevMonth()"><<</a>',
            '<span class="date-picker-current-month">{{month}}</span>',
            '<a class="date-picker-next" ng-click="nextMonth()">>></a>',
          '</div>',
          '<div class="date-picker-day-header">',
            '<div class="date-picker-day" ng-repeat="dayName in dayNames">{{dayName}}</div>',
          '</div>',
          '<div class="date-picker-month">',
            '<div class="date-picker-week" ng-repeat="week in calendar">',
              '<div class="date-picker-day ',
                'isSameMonth-{{day.isSameMonth}} isToday-{{day.isToday}} isSelected-{{day.isSelected}}"', 
                'ng-repeat="day in week"',
                'ng-click="selectDate(day)">{{day.dayOfWeek}}</div>',
            '</div>',
          '</div>',
        '</div>'
      ].join(''),
      link: function($scope, element, attrs) {
        var firstDate = moment(new Date());
        var today = moment(new Date());
        var selectDate;
        var selectDateCallback = $scope.$parent[attrs["selectDate"].split("(")[0]];

        var firstDayOfWeek = parseInt(attrs["firstDayOfWeek"]) || 0;

        $scope.selectDate = function(day) {
          if(selectDate != undefined) {
            selectDate.isSelected = false;
          }
          selectDate = day;
          day.isSelected = true;
          if(selectDateCallback !== undefined) {
            selectDateCallback(day);
          }
        }

        $scope.dayNames = [];
        for (var i = firstDayOfWeek; i < firstDayOfWeek+7; i++) {
          $scope.dayNames.push(
            moment(new Date()).startOf('week').add('days', i).format('ddd')
          );
        };
        
        function updateMonth(date) {
          $scope.month = date.format("MMMM - YYYY");
        }
        
        updateMonth(firstDate);
        buildCalendar(firstDate);
        
        $scope.prevMonth = function() {
          firstDate.subtract('months', 1);
          updateMonth(firstDate);
          buildCalendar(firstDate);
        }
        
        $scope.nextMonth = function() {
          firstDate.add('months', 1);
          updateMonth(firstDate);
          buildCalendar(firstDate);
        }
        
        function buildCalendar(current) {
          current = current.clone();
          current.startOf('month');//1er jour du mois
          var calendar = [];
          var date = current.clone();
          date.subtract('days', current.format("d") - firstDayOfWeek);//1er lundi avant
          var allDays = 0;
          var week = 0;
          var endOfMonth = current.clone();
          endOfMonth.endOf('month');
          while(date.isBefore(endOfMonth)) {
            if(allDays % 7 == 0 && allDays != 0) {
              week++;//new week
            }
            calendar[week] = calendar[week] || [];
            calendar[week].push({
              moment: date.clone(),
              dayOfWeek: date.format('D'),
              isSameMonth: date.month() == current.month(),
              isToday: date.month() == today.month() && date.date() == today.date() && date.year() == today.year()
            });
            date.add('days', 1);
            allDays++;
          }
          while(date.format("d") != firstDayOfWeek) {
            calendar[week].push({
              moment: date.clone(),
              dayOfWeek: date.format('D'),
              isSameMonth: false,
              isToday: false
            });
            date.add('days', 1);
          }
          $scope.calendar = calendar;
        }

      }
    }
    
  }]);

})();