using my.bookshop as my from '../db/data-model';

service CatalogService {
    //@readonly
    @cds.persistence.skip
    @odata.draft.enabled
    entity Books @Insertable as projection on my.Books;
    @cds.persistence.skip
    entity Authors @Insertable @Updateable as projection on my.Authors;
    //annotate Books with @odata.draft.enabled;
    
    @cds.persistence.skip
    entity RandomEntity @Insertable @Updateable as projection on my.RandomEntity;
}

annotate my.Authors with @(
	UI.SelectionFields: [placeOfBirth],
    UI.LineItem: [
        //{$Type: 'UI.DataField', Value: ID, Label: 'MyId'},
        {$Type: 'UI.DataField', Value:name, Label: 'Author'},
        {$Type: 'UI.DataField', Value:placeOfBirth, Label: 'Place of Birth'}
    ],

	UI.Facets: [
		{	$Type:'UI.ReferenceFacet', Label:'IdFacet', Target:'@UI.FieldGroup#IdFacet'},
		{	$Type:'UI.ReferenceFacet', Label:'General', Target:'@UI.FieldGroup#General'}
	],
	
	UI.FieldGroup#General: {
		Data:[
			{Value: name},
			{Value: placeOfBirth}
		]
	},
	UI.FieldGroup#IdFacet: {
		Data:[
			{Value: ID}
		]
	},
	UI.HeaderInfo: {
		TypeName: 'Author',
		TypeNamePlural: 'Authors',
		Title: 'Title_Authors',
		Description : 'Desc_Authors'
	}
);