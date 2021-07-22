CREATE DATABASE [Volcano_Island_Reservations];

USE [Volcano_Island_Reservations];

CREATE TABLE [dbo].[Reservations]
(
    [id]             [bigint] IDENTITY,
    [full_name]      [varchar](100) NOT NULL,
    [email]          [varchar](100) NOT NULL,
    [arrival_date]   [date]         NOT NULL,
    [departure_date] [date]         NOT NULL,
    [created_at]     [datetime]     NOT NULL,
    [updated_at]     [datetime]     NULL,
    [cancelled_at]   [datetime]     NULL,
    [status]         [int]          NOT NULL,
    CONSTRAINT [PK_RESERVATIONS] PRIMARY KEY CLUSTERED ([id] ASC) ON [PRIMARY]
);

CREATE TABLE [dbo].[ReservedDays]
(
    [id]             [bigint] IDENTITY,
    [day]            [date] NOT NULL,
    [reservation_id] [bigint] FOREIGN KEY REFERENCES Reservations (id)
    CONSTRAINT [PK_ReservedDays] PRIMARY KEY CLUSTERED (id ASC) ON [PRIMARY],
    CONSTRAINT [UQ_ReservedDays_DAY] UNIQUE NONCLUSTERED (day ASC) ON [PRIMARY]
);
