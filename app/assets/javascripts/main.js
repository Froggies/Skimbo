'use strict';

require([
  'app',
  'services/StringUtils', 
  'services/ArrayUtils', 
  'services/UnifiedRequestUtils', 
  'services/ImagesUtils', 
  'services/Visibility', 
  'services/ColumnSize', 
  'services/Network', 
  'services/PopupProvider', 
  'services/DragAndDropColumns', 
  'services/ServerCommunication', 
  'filters/translation', 
  'controllers/NotificationController', 
  'controllers/MainController', 
  'controllers/ModifColumnController', 
  'controllers/HeaderController', 
  'controllers/ColumnController'
], function(app) {
  window.angular.bootstrap(window.document.getElementById('publicApp'), ['publicApp']);
});