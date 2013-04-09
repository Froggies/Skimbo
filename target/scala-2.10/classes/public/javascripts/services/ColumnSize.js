'use strict';

define(["app"], function(app) {

app.factory("ColumnSize", function() {

  var maxWidthNbColumns = 6;
  var maxHeightNbColumns = 4;

  function widthManagement(column) {
    // fix bornes
    if(column.width < 1) {
      column.width = 2;
    } else if(column.width > maxWidthNbColumns) {
      column.width = maxWidthNbColumns;
    }
    // finally set size
    var w = column.width * 100 / maxWidthNbColumns;
    column.csswidth = (w - 1) + '%';
  }

  function heightManagement(column) {
    // fix bornes
    if(column.height < 1) {
      column.height = 2;
    } else if(column.height > maxHeightNbColumns) {
      column.height = maxHeightNbColumns;
    }
    // finally set size
    var h = column.height * 100 / maxHeightNbColumns;
    column.cssheight = (h - 1) + '%';
  }

  return {
    setSize: function(columns) {
      var size = columns.length;
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

});

return app;
});