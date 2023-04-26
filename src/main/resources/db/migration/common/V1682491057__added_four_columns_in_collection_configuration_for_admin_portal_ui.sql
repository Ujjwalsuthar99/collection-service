ALTER TABLE collection.collection_configurations add column if not exists modified_date timestamptz;

ALTER TABLE collection.collection_configurations add column if not exists modified_by varchar;

ALTER TABLE collection.collection_configurations add column if not exists active bool default true;

ALTER TABLE collection.collection_configurations add column if not exists stage varchar;