CREATE DATABASE [Volcano_Island_Reservations];

USE [Volcano_Island_Reservations];

CREATE TABLE [dbo].[Reservations]
(
    [id]                  [bigint]                                   IDENTITY,
    [full_name]           [varchar](100)                             NOT NULL,
    [email]               [varchar](100)                             NOT NULL,
    [arrival_date]        [date]                                     NOT NULL,
    [departure_date]      [date]                                     NOT NULL,
    [created_at]          [datetime]                                 NOT NULL,
    [updated_at]          [datetime]                                 NULL,
    [cancelled_at]        [datetime]                                 NULL,
    [status]              [int]                                      NOT NULL,
    CONSTRAINT [PK_RESERVATIONS] PRIMARY KEY CLUSTERED ([id] ASC)  ON [PRIMARY]
    )


CREATE TABLE [dbo].[ReservedDays]
(
    [id]                  [bigint]                                   IDENTITY,
    [day]                 [date]                                     NOT NULL,
    [reservation_id]      [bigint]                                   FOREIGN KEY REFERENCES Reservations(id)
    CONSTRAINT [PK_ReservedDays] PRIMARY KEY CLUSTERED(id ASC)  ON [PRIMARY],
    CONSTRAINT [UQ_ReservedDays_DAY] UNIQUE NONCLUSTERED (day ASC) ON [PRIMARY]
    )

-- INSERT RESERVATION 1
    SET IDENTITY_INSERT [dbo].[Reservations] ON;

INSERT [dbo].[Reservations] ([id], [full_name], [email], [arrival_date], [departure_date], [created_at], [status]) VALUES
(10001, 'Jose Perez', 'jose@mail.com', '2030-02-01', '2030-02-02', '2030-01-15T14:14:57', 1);

SET IDENTITY_INSERT [dbo].[Reservations] OFF;

SET IDENTITY_INSERT [dbo].[ReservedDays] ON;

INSERT [dbo].[ReservedDays] ([id], [day], [reservation_id]) VALUES
(100001, '2030-02-01', 10001),
(100002, '2030-02-02', 10001);

SET IDENTITY_INSERT [dbo].[ReservedDays] OFF;

-- INSERT RESERVATION 2
SET IDENTITY_INSERT [dbo].[Reservations] ON;

INSERT [dbo].[Reservations] ([id], [full_name], [email], [arrival_date], [departure_date], [created_at], [status]) VALUES
(10002, 'Daniel Perez', 'daniel@mail.com', '2030-01-10', '2030-01-12', '2030-01-09T14:14:57', 1);

SET IDENTITY_INSERT [dbo].[Reservations] OFF;

SET IDENTITY_INSERT [dbo].[ReservedDays] ON;

INSERT [dbo].[ReservedDays] ([id], [day], [reservation_id]) VALUES
(100003, '2030-01-10', 10002),
(100004, '2030-01-11', 10002),
(100005, '2030-01-12', 10002);

SET IDENTITY_INSERT [dbo].[ReservedDays] OFF;

