CREATE TABLE [dbo].[Uporabnik] (
    [username] VARCHAR (50) NOT NULL,
    [ime]      VARCHAR (50) NULL,
    [priimek]  VARCHAR (50) NULL,
    [geslo]    VARCHAR (50) NOT NULL,
    [admin]    BIT          NOT NULL,
    PRIMARY KEY CLUSTERED ([username] ASC)
);

CREATE TABLE [dbo].[Pogovor] (
    [Id]       INT          IDENTITY (1, 1) NOT NULL,
    [username] VARCHAR (50) NOT NULL,
    [besedilo] TEXT         NULL,
    [time]     DATETIME     NOT NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC),
    CONSTRAINT [FK_Pogovor_Uporabnik] FOREIGN KEY ([username]) REFERENCES [dbo].[Uporabnik] ([username])
);