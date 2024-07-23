ALTER TABLE ONLY collection.collection_limit_userwise
    ADD CONSTRAINT collection_limit_userwise_un UNIQUE (user_id, collection_limit_strategies_key);