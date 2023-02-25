ALTER TABLE collection.collection_limit_userwise
RENAME COLUMN collection_limit_definitions_id TO collection_limit_userwise_id;

ALTER TABLE collection.collection_activity_logs ALTER COLUMN images DROP NOT NULL;

ALTER TABLE collection.receipt_transfer ALTER COLUMN receipt_image DROP NOT NULL;