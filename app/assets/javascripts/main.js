'use strict';

require([
  'app',
  'filters/translation',
  'filters/maxRange',
  'services/StringUtils',
  'services/Visibility',
  'services/ArrayUtils',
  'services/UnifiedRequestUtils',
  'services/ImagesUtils',
  'services/ColumnSize',
  'services/Network',
  'services/PopupProvider',
  'services/ServerCommunication',
  'directives/Drag',
  'directives/ScrollManager',
  'directives/ShowOnHoverParent',
  'directives/OnKeyPress',
  'directives/Oracle',
  'directives/Focus',
  'controllers/NotificationController',
  'controllers/MediasController',
  'controllers/MainController',
  'controllers/ModifColumnController',
  'controllers/HeaderController',
  'controllers/AccountController',
  'controllers/ColumnController',
  'controllers/PostController'
], function(app) {
  window.angular.bootstrap(window.document.getElementById('publicApp'), ['publicApp']);
});
