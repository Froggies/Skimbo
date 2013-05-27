'use strict';

define(["app"], function(app) {

/**
* Think to change mobile.less if you change this file ! 
**/

app.factory("ColumnSize", ["$window", function($window) {

  var minSizeWidthColumn = 100;
  var minSizeHeightColumn = 100;
  var maxWidthNbColumns = 6;
  var maxHeightNbColumns = 4;

  function calculNbColumn() {
    var tempNbWidth = Math.floor($window.innerWidth / minSizeWidthColumn);
    if(tempNbWidth >= 6) {
      maxWidthNbColumns = 6;
      maxHeightNbColumns = 4;
    } else {
      maxWidthNbColumns = 1;
      maxHeightNbColumns = 1;
    }
  }

  function widthManagement(column) {
    // fix bornes
    if(column.width < 1) {
      column.width = 2;
    } else if(maxWidthNbColumns > 1 && column.width > maxWidthNbColumns) {
      column.width = maxWidthNbColumns;
    }
    // finally set size
    if(maxWidthNbColumns <= 5) {
      column.csswidth = ($window.innerWidth - 51)+'px';
    } else {
      var w = column.width * 100 / maxWidthNbColumns;
      column.csswidth = (w - 1.2) + '%';
    }
  }

  function heightManagement(column) {
    // fix bornes
    if(column.height < 1) {
      column.height = 2;
    } else if(maxHeightNbColumns > 1 && column.height > maxHeightNbColumns) {
      column.height = maxHeightNbColumns;
    }
    // finally set size
    if(maxHeightNbColumns <= 3) {
      column.cssheight = '100%';
    } else {
      var h = column.height * 100 / maxHeightNbColumns;
      column.cssheight = (h - 1) + '%';
    }
  }

  return {
    isMobileSize: function() {
      return maxWidthNbColumns != 6;
    },
    setSize: function(columns) {
      var size = columns.length;
      calculNbColumn();
      for (var i = 0; i < size; i++) {
        widthManagement(columns[i]);
        heightManagement(columns[i]);
      };
    },
    buildSizeCompo: function(columns) {
      var size = columns.length;
      var column;
      for (var i = 0; i < size; i++) {
        column = columns[i];
        column.compoSize = [];
        for (var h = 1; h <= maxHeightNbColumns; h++) {
          column.compoSize.push({height:h, tab:[]});
          for (var w = 1; w <= maxWidthNbColumns; w++) {
            var b = column.height >= h && column.width >= w;
            var selectedStyle = "";
            if(b == true) {
              selectedStyle = "color:red";
            }
            column.compoSize[h-1].tab.push({width:w, selected:b, 'selectedStyle':selectedStyle});
          };
        };
      };
    },
    resizeColumn: function(column, height, width) {
      column.width = width;
      column.height = height;
      widthManagement(column);
      heightManagement(column);
      this.buildSizeCompo([column]);
      return;
    }
  }

}]);

return app;
});