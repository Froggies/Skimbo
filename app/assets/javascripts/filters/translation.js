'use strict';

publicApp.filter('i18n', ['$rootScope', function($rootScope) {
    return function (input) {
        var translations = {
            'fr' : {
                'ADD_NEW_COLUMN' : 'Ajouter une colonne',
                'ADD_STREAM_FOR_SOCIAL_NETWORK' : 'Ajouter un flux pour : ',
                'ALL_FIELD_REQUIRED' : 'Tous les champs sont requis.',
                'ALREADY_IN_YOUR_COLUMN' : 'Déjà présent dans cette colonne :',
                'AVAILABLE_SERVICES' : 'Services disponibles :',
                'AVAILABLE_SOCIAL_NETWORKS' : 'Réseaux sociaux disponibles :',
                'CANCEL' : 'Annuler',
                'CANT_ADD_SAME_SERVICE' : 'Vous ne pouvez pas ajouter deux fois le même service.',
                'CHANGE' : 'Modifier',
                'COLUMN_TITLE' : 'Titre de la colonne :',
                'COLUMNS' : 'Colonnes',
                'CREATE' : 'Créer !',
                'DELETE' : 'Supprimer',
                'MESSAGES' : 'Messages',
                'TITLE_ALREADY_EXISTS' : 'Ce titre existe déjà.',
                'TITLE_REQUIRED' : 'Le titre est requis.',
                'TO_ADD_STREAM_IN_COLUMN' : 'Pour ajouter un flux à cette colonne, sélectionnez un ou plusieurs services ci-dessus.',
                'YOU_ARE_RECEIVING' : 'Vous recevez'
            },
            'en' : {
                'ADD_NEW_COLUMN' : 'Add new column',
                'ADD_STREAM_FOR_SOCIAL_NETWORK' : 'Add a stream for this social network :',
                'ALL_FIELD_REQUIRED' : 'All fields are required.',
                'ALREADY_IN_YOUR_COLUMN' : 'Already in your column :',
                'AVAILABLE_SERVICES' : 'Services available:',
                'AVAILABLE_SOCIAL_NETWORKS' : 'Social networks available :',
                'CANCEL' : 'Cancel',
                'CANT_ADD_SAME_SERVICE' : 'You can\'t add twice same services.',
                'CHANGE' : 'Change',
                'COLUMN_TITLE' : 'Column title :',
                'COLUMNS' : 'Columns',
                'CREATE' : 'Create !',
                'DELETE' : 'Delete',
                'MESSAGES' : 'Messages',
                'TITLE_ALREADY_EXISTS' : 'This title already exists.',
                'TITLE_REQUIRED' : 'The title is required !',
                'TO_ADD_STREAM_IN_COLUMN' : 'To add a stream in this column, select one or many services above.',
                'YOU_ARE_RECEIVING' : 'You are receiving'
            }
        },
        currentLanguage = $rootScope.currentLanguage || 'en';

        return translations[currentLanguage][input];
    }

}]);