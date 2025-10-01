CREATE TABLE meals (
                       id UUID PRIMARY KEY,
                       user_id UUID NOT NULL,
                       meal_type VARCHAR(20) NOT NULL CHECK (meal_type IN ('BREAKFAST','LUNCH','DINNER','SNACK')),
                       description TEXT NOT NULL,
                       calories INT NOT NULL CHECK (calories >= 0),
                       protein_g NUMERIC,
                       carbs_g NUMERIC,
                       fat_g NUMERIC,
                       logged_at TIMESTAMP NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                       note TEXT
);

CREATE TABLE meal_categories (
                                 id SERIAL PRIMARY KEY,
                                 name VARCHAR(100) NOT NULL,
                                 description TEXT
);

CREATE TABLE meal_category_links (
                                     meal_id UUID REFERENCES meals(id) ON DELETE CASCADE,
                                     category_id INT REFERENCES meal_categories(id),
                                     PRIMARY KEY(meal_id, category_id)
);
