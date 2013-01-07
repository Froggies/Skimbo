'use strict';

require.config({
  baseUrl: '/assets/javascripts/',
  paths: {
    'angular': 'angular',
    'angularSanitize': 'angularSanitize'
  },
  shim: {
    'angular' : {'exports' : 'angular'},
    'angularSanitize' : {deps:['angular']}
  },
  priority: [
    "angular"
  ]
});

require([
  'angular',
  'angularSanitize',
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
], function(angular, app) {
  angular.bootstrap(window.document.getElementById('publicApp'), ['publicApp']);
});