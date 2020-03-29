namespace my.bookshop;

@cds.persistence.skip
entity Books {
  key ID : Integer;
  title  : String;
  stock  : Integer;
  author : Association to Authors;
}

@cds.persistence.skip
entity Authors {
	key ID : Integer;
	name : String;
	placeOfBirth : String;
	books : Association to many Books on books.author = $self;
}

@cds.persistence.skip
entity RandomEntity {
	key ID : Integer;
	name : String;
}