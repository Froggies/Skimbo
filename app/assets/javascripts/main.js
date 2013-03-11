'use strict';

require([
  'app',
  'filters/translation', 
  'services/StringUtils', 
  'services/ArrayUtils', 
  'services/UnifiedRequestUtils', 
  'services/ImagesUtils', 
  'services/Visibility', 
  'services/ColumnSize', 
  'services/Network', 
  'services/PopupProvider', 
  'services/ServerCommunication', 
  'directives/DragAndDrop',
  'directives/ScrollManager',
  'directives/ShowOnHoverParent',
  'controllers/NotificationController', 
  'controllers/MainController', 
  'controllers/ModifColumnController', 
  'controllers/HeaderController', 
  'controllers/ColumnController',
  'controllers/PostController'
], function(app) {
  window.angular.bootstrap(window.document.getElementById('publicApp'), ['publicApp']);
});