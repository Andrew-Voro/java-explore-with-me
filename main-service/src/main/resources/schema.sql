create TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255)  NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
  );

create TABLE IF NOT EXISTS categories (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT pk_category PRIMARY KEY (id),
  CONSTRAINT UQ_CATEGORIES_NAME UNIQUE (name)
  );

create TABLE IF NOT EXISTS events (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  annotation TEXT NOT NULL,
  category  BIGINT  NOT NULL,
  confirmed_requests BIGINT,
  created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  description TEXT NOT NULL,
  event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  initiator BIGINT  NOT NULL,
  lat REAL NOT NULL,
  lon REAL NOT NULL,
  paid Boolean,
  participant_limit BIGINT,
  published_on  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  request_moderation Boolean,
  state_action VARCHAR(20),
  title VARCHAR(120),
  views BIGINT,
  CONSTRAINT pk_event PRIMARY KEY (id),
  CONSTRAINT UQ_EVENTS_NAME UNIQUE (title),
  CONSTRAINT fk_events_to_users FOREIGN KEY(initiator) REFERENCES users(id),
  CONSTRAINT fk_events_to_categories FOREIGN KEY(category) REFERENCES categories(id)
  );

create TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  event_id BIGINT  NOT NULL,
  requester BIGINT  NOT NULL,
  status VARCHAR(20),
  CONSTRAINT pk_request PRIMARY KEY (id),
  CONSTRAINT fk_requests_to_events FOREIGN KEY(event_id) REFERENCES events(id),
  CONSTRAINT fk_requests_to_users FOREIGN KEY(requester) REFERENCES users(id)
  );

  create TABLE IF NOT EXISTS compilations (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  title VARCHAR(200)NOT NULL,
--  event BIGINT  NOT NULL,
  pinned Boolean,
  CONSTRAINT pk_compilations PRIMARY KEY (id)
-- , CONSTRAINT fk_compilations_to_events FOREIGN KEY(event) REFERENCES events(id)
  );

  create TABLE IF NOT EXISTS compilation_event (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  event_id BIGINT ,
  compilation_id BIGINT,
  CONSTRAINT pk_compilations_event PRIMARY KEY (id)
  /*, CONSTRAINT fk_compilation_event_to_events FOREIGN KEY(event_id) REFERENCES events(id),
  CONSTRAINT fk_compilation_event_to_compilation FOREIGN KEY(compilation_id) REFERENCES compilations(id)*/
  );

