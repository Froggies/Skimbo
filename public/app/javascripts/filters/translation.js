'use strict';

define(["app"], function(app) {

app.filter('i18n', ['$rootScope', function($rootScope) {

  var translations = {
    'fr' : {
      'ADD_NEW_COLUMN' : 'Ajouter une colonne',
      'ADD_STREAM_FOR_SOCIAL_NETWORK' : 'Ajouter un flux pour : ',
      'ALL_FIELD_REQUIRED' : 'Tous les champs sont requis.',
      'ALREADY_IN_YOUR_COLUMN' : 'Déjà présent dans cette colonne :',
      'AVAILABLE_SERVICES' : 'Services disponibles :',
      'AVAILABLE_SOCIAL_NETWORKS' : 'Réseaux sociaux disponibles :',
      'BAD_DATE_POSTER' : 'La date entrée n\'est pas valide.',
      'CANCEL' : 'Annuler',
      'CANT_ADD_SAME_SERVICE' : 'Vous ne pouvez pas ajouter deux fois le même service.',
      'CHANGE' : 'Modifier',
      'COLUMN_TITLE' : 'Titre de la colonne :',
      'COLUMN' : 'Colonne',
      'COLUMNS' : 'Colonnes',
      'COMMENT' : 'Commenter',
      'CONFIGURATION_COLUMN' : 'Préférences de la colonne',
      'CONNECTED' : 'Vous êtes connecté à :',
      'CONTACT_US' : 'Nous contacter',
      'CONTENT_REQUIRED' : 'Le message est requis.',
      'CREATE' : 'Créer',
      'DELETE' : 'Supprimer',
      'GET_TOKEN_PROGRESS' : 'Token',
      'LOG_OUT' : 'Déconnexion',
      'MESSAGES' : 'Messages',
      'NEW_MGS_UNREAD' : 'nouveaux messages. Clic pour tout lire.',
      'PARAM_HELPER_SEARCH' : 'Recherche',
      'POSTER_REQUIRED' : 'Au moins un réseau est requis.',
      'REFRESH_DETAILS' : 'Rafraîchir/Voir les détails',
      'RESIZE_COLUMN' : 'Modifier la taille de la colonne.',
      'RETRIEVING_MESSAGES' : 'Récupération des messages',
      'SCROLL_TO_SHOW_MSG' : 'Vous avez des nouveaux messages, montez l\'ascenseur pour les voir.',
      'SEND' : 'Envoyer',
      'SEND_LATER' : 'Planifier',
      'SHARE' : 'Partager',
      'STAR' : 'Marquer ce contenu (retweet, like, thank...)',
      'TITLE_ALREADY_EXISTS' : 'Ce titre existe déjà.',
      'TITLE_REQUIRED' : 'Le titre est requis.',
      'TO_ADD_STREAM_IN_COLUMN' : 'Pour ajouter un flux à cette colonne, sélectionnez un ou plusieurs services ci-dessus.',
      'YOU_ARE_RECEIVING' : 'Vous recevez',
      //POST PLACEHOLDERS
      'PH_POST_TITLE' : 'Titre',
      'PH_POST_MESSAGE' : 'Message',
      'PH_POST_URL' : 'Partager un lien',
      'PH_POST_URLIMAGE' : 'Partager une image',
      //ERRORS
      'CLICK_TO_HIDE' : 'Cliquer pour cacher cette erreur.',
      'CLICK_TO_RECONNECT' : 'Cliquer pour être reconnecté.',
      'DISCONNECT' : 'Vous êtes déconnecté de ',
      'RateLimit' : 'Vous utilisez trop de services de ',
      'Timeout' : 'Les serveurs ne répondent pas chez ',
      'Unknown' : 'Erreur inconnue de ',
      'Parser' : 'Un message est non conforme de ',
      'NoParser' : 'Il n\'existe pas de parser pour ',
      'Post' : 'Le message n\'a pas pu être envoyé chez ',
      'Star' : 'Le message n\'a pas pu être marqué chez ',
      'Comment' : 'Le message n\'a pas pu être commenté chez ',
      'EmailNotSend' : 'Votre email n\'a pas pu être envoyé à l\'équipe de '
    },
    'en' : {
      'ADD_NEW_COLUMN' : 'Add new column',
      'ADD_STREAM_FOR_SOCIAL_NETWORK' : 'Add a stream for this social network :',
      'ALL_FIELD_REQUIRED' : 'All fields are required.',
      'ALREADY_IN_YOUR_COLUMN' : 'Already in your column :',
      'AVAILABLE_SERVICES' : 'Services available:',
      'AVAILABLE_SOCIAL_NETWORKS' : 'Social networks available :',
      'BAD_DATE_POSTER' : 'Date isn\'t valid.',
      'CANCEL' : 'Cancel',
      'CANT_ADD_SAME_SERVICE' : 'You can\'t add twice same services.',
      'CHANGE' : 'Change',
      'COLUMN_TITLE' : 'Column title :',
      'COMMENT' : 'Comment',
      'COLUMN' : 'Column',
      'COLUMNS' : 'Columns',
      'CONFIGURATION_COLUMN' : 'Column\'s configuration.',
      'CONNECTED' : 'You are logged in:',
      'CONTACT_US' : 'Contact us',
      'CONTENT_REQUIRED' : 'The message is required !',
      'CREATE' : 'Create',
      'DELETE' : 'Delete',
      'GET_TOKEN_PROGRESS' : 'Token',
      'LOG_OUT' : 'Log out',
      'MESSAGES' : 'Messages',
      'NEW_MGS_UNREAD' : 'new messages. Click to read all.',
      'PARAM_HELPER_SEARCH' : 'Search',
      'POSTER_REQUIRED' : 'At least one network is required.',
      'REFRESH_DETAILS' : 'Refresh/See details',
      'RESIZE_COLUMN' : 'Resize column',
      'RETRIEVING_MESSAGES' : 'Retrieving messages',
      'SCROLL_TO_SHOW_MSG' : 'You have new messages, scroll to see them.',
      'SEND' : 'Send',
      'SEND_LATER' : 'Delayed',
      'SHARE' : 'Share',
      'STAR' : 'Retweet, like, thank...',
      'TITLE_ALREADY_EXISTS' : 'This title already exists.',
      'TITLE_REQUIRED' : 'The title is required !',
      'TO_ADD_STREAM_IN_COLUMN' : 'To add a stream in this column, select one or many services above.',
      'YOU_ARE_RECEIVING' : 'You are receiving',
      //POST PLACEHOLDERS
      'PH_POST_TITLE' : 'Title',
      'PH_POST_MESSAGE' : 'Message',
      'PH_POST_URL' : 'Share a link',
      'PH_POST_URLIMAGE' : 'Share a picture',
      //ERRORS
      'CLICK_TO_HIDE' : 'Click here to hide error.',
      'CLICK_TO_RECONNECT' : 'Click here to be connected again.',
      'DISCONNECT' : 'You have been disconnected from ',
      'RateLimit' : 'You use too many services of ',
      'Timeout' : 'The servers do not respond in ',
      'Unknown' : 'Unknown error from ',
      'Parser' : 'Not parsable message from ',
      'NoParser' : 'No parser found for ',
      'Post' : 'Post can\'t be send to ',
      'Star' : 'Star can\'t be send to ',
      'Comment' : 'Comment can\'t be send to ',
      'EmailNotSend' : 'Your mail couldn\'t be send at '
    }
  };

  return function (input) {
    var currentLanguage = $rootScope.currentLanguage || 'en';
    if(translations[currentLanguage] != undefined &&
      translations[currentLanguage][input] != undefined) {
      return translations[currentLanguage][input];
    } else {
      return translations["en"][input];
    }
  }

}]);

return app;
});
