/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Skeleton Program code for the AQA A Level Paper 1 2017 examination this code
 * should be used In conjunction With the Preliminary Material written by the
 * AQA Programmer Team developed in the NetBeans IDE 8.1 programming environment
 *
 * Additional file AQAConsole2017 is used.
 *
 * A package name may be chosen and private and public modifiers added
 * Permission to make these changes to the Skeleton Program does not need to be
 * obtained from AQA/AQA Programmer
 *
 * Version 1.1 released January 2017
 *
 *
 */
package main;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Simulation Sim;
        int MenuOption;
        int LandscapeSize;
        int InitialWarrenCount;
        int InitialFoxCount;
        int Variability;
        Boolean FixedInitialLocations;
        do {
            Console.println("Predator Prey Simulation Main Menu");
            Console.println();
            Console.println("1. Run simulation with default settings");
            Console.println("2. Run simulation with custom settings");
            Console.println("3. Exit");
            Console.println();
            MenuOption = Console.readInteger("Select option: ");
            if (MenuOption == 1 || MenuOption == 2) {
                if (MenuOption == 1) {
                    LandscapeSize = 15;
                    InitialWarrenCount = 5;
                    InitialFoxCount = 5;
                    Variability = 0;
                    FixedInitialLocations = true;
                } else {
                    LandscapeSize = Console.readInteger("Landscape Size: ");
                    InitialWarrenCount = Console.readInteger("Initial number of warrens: ");
                    InitialFoxCount = Console.readInteger("Initial number of foxes: ");
                    Variability = Console.readInteger("Randomness variability (percent): ");
                    FixedInitialLocations = false;
                }
                Sim = new Simulation(LandscapeSize, InitialWarrenCount, InitialFoxCount, Variability, FixedInitialLocations);
            }
        } while (MenuOption != 3);
        Console.readLine();
    }
}

class Location {

    public Fox Fox;
    public Warren Warren;

    public Location() {
        Fox = null;
        Warren = null;
    }
}

class Simulation {

    private Location[][] Landscape;
    private int TimePeriod = 0;
    private int WarrenCount = 0;
    private int FoxCount = 0;
    private Boolean ShowDetail = false;
    private int LandscapeSize;
    private int Variability;
    private static Random Rnd = new Random();

    public Simulation(int LandscapeSize, int InitialWarrenCount,
            int InitialFoxCount, int Variability, Boolean FixedInitialLocations) {
        int MenuOption;
        int x;
        int y;
        String ViewRabbits;
        this.LandscapeSize = LandscapeSize;
        this.Variability = Variability;
        Landscape = new Location[LandscapeSize][LandscapeSize];
        CreateLandscapeAndAnimals(InitialWarrenCount, InitialFoxCount, FixedInitialLocations);
        DrawLandscape();
        do {
            Console.println();
            Console.println("1. Advance to next time period showing detail");
            Console.println("2. Advance to next time period hiding detail");
            Console.println("3. Inspect fox");
            Console.println("4. Inspect warren");
            Console.println("5. Exit");
            Console.println();
            MenuOption = Console.readInteger("Select option: ");
            if (MenuOption == 1) {
                TimePeriod += 1;
                ShowDetail = true;
                AdvanceTimePeriod();
            }
            if (MenuOption == 2) {
                TimePeriod += 1;
                ShowDetail = false;
                AdvanceTimePeriod();
            }
            if (MenuOption == 3) {
                x = InputCoordinate('x');
                y = InputCoordinate('y');
                if (Landscape[x][y].Fox != null) {
                    Landscape[x][y].Fox.Inspect();
                }
            }
            if (MenuOption == 4) {
                x = InputCoordinate('x');
                y = InputCoordinate('y');
                if (Landscape[x][y].Warren != null) {
                    Landscape[x][y].Warren.Inspect();
                    ViewRabbits = Console.readLine("View individual rabbits (y/n)?");
                    if (ViewRabbits.equals("y")) {
                        Landscape[x][y].Warren.ListRabbits();
                    }
                }
            }
        } while ((WarrenCount > 0 || FoxCount > 0) && MenuOption != 5);
        Console.readLine();
    }

    private int InputCoordinate(char CoordinateName) {
        int Coordinate;
        Coordinate = Console.readInteger("  Input " + CoordinateName + " coordinate: ");
        return Coordinate;
    }

    private void AdvanceTimePeriod() {
        int NewFoxCount = 0;
        if (ShowDetail) {
            Console.println();
        }
        for (int x = 0; x < LandscapeSize; x++) {
            for (int y = 0; y < LandscapeSize; y++) {
                if (Landscape[x][y].Warren != null) {
                    if (ShowDetail) {
                        Console.println("Warren at (" + x + "," + y + "):");
                        Console.print("  Period Start: ");
                        Landscape[x][y].Warren.Inspect();
                    }
                    if (FoxCount > 0) {
                        FoxesEatRabbitsInWarren(x, y);
                    }
                    if (Landscape[x][y].Warren.NeedToCreateNewWarren()) {
                        CreateNewWarren();
                    }
                    Landscape[x][y].Warren.AdvanceGeneration(ShowDetail);
                    if (ShowDetail) {
                        Console.print("  Period End: ");
                        Landscape[x][y].Warren.Inspect();
                        Console.readLine();
                    }
                    if (Landscape[x][y].Warren.WarrenHasDiedOut()) {
                        Landscape[x][y].Warren = null;
                        WarrenCount -= 1;
                    }
                }
            }
        }
        for (int x = 0; x < LandscapeSize; x++) {
            for (int y = 0; y < LandscapeSize; y++) {
                if (Landscape[x][y].Fox != null) {
                    if (ShowDetail) {
                        Console.println("Fox at (" + x + "," + y + "): ");
                    }
                    Landscape[x][y].Fox.AdvanceGeneration(ShowDetail);
                    if (Landscape[x][y].Fox.CheckIfDead()) {
                        Landscape[x][y].Fox = null;
                        FoxCount -= 1;
                    } else {
                        if (Landscape[x][y].Fox.ReproduceThisPeriod()) {
                            if (ShowDetail) {
                                Console.println("  Fox has reproduced. ");
                            }
                            NewFoxCount += 1;
                        }
                        if (ShowDetail) {
                            Landscape[x][y].Fox.Inspect();
                        }
                        Landscape[x][y].Fox.ResetFoodConsumed();
                    }
                }
            }
        }
        if (NewFoxCount > 0) {
            if (ShowDetail) {
                Console.println("New foxes born: ");
            }
            for (int f = 0; f < NewFoxCount; f++) {
                CreateNewFox();
            }
        }
        if (ShowDetail) {
            Console.readLine();
        }
        DrawLandscape();
        Console.println();
    }

    private void CreateLandscapeAndAnimals(int InitialWarrenCount,
            int InitialFoxCount, Boolean FixedInitialLocations) {
        for (int x = 0; x < LandscapeSize; x++) {
            for (int y = 0; y < LandscapeSize; y++) {
                Landscape[x][y] = new Location();
            }
        }
        if (FixedInitialLocations) {
            Landscape[1][1].Warren = new Warren(Variability, 38);
            Landscape[2][8].Warren = new Warren(Variability, 80);
            Landscape[9][7].Warren = new Warren(Variability, 20);
            Landscape[10][3].Warren = new Warren(Variability, 52);
            Landscape[13][4].Warren = new Warren(Variability, 67);
            WarrenCount = 5;
            Landscape[2][10].Fox = new Fox(Variability);
            Landscape[6][1].Fox = new Fox(Variability);
            Landscape[8][6].Fox = new Fox(Variability);
            Landscape[11][13].Fox = new Fox(Variability);
            Landscape[12][4].Fox = new Fox(Variability);
            FoxCount = 5;
        } else {
            for (int w = 0; w < InitialWarrenCount; w++) {
                CreateNewWarren();
            }
            for (int f = 0; f < InitialFoxCount; f++) {
                CreateNewFox();
            }
        }
    }

    private void CreateNewWarren() {
        int x;
        int y;
        do {
            x = Rnd.nextInt(LandscapeSize);
            y = Rnd.nextInt(LandscapeSize);
        } while (Landscape[x][y].Warren != null);
        if (ShowDetail) {
            Console.println("New Warren at (" + x + "," + y + ")");
        }
        Landscape[x][y].Warren = new Warren(Variability);
        WarrenCount += 1;
    }

    private void CreateNewFox() {
        int x;
        int y;
        do {
            x = Rnd.nextInt(LandscapeSize);
            y = Rnd.nextInt(LandscapeSize);
        } while (Landscape[x][y].Fox != null);
        if (ShowDetail) {
            Console.println("  New Fox at (" + x + "," + y + ")");
        }
        Landscape[x][y].Fox = new Fox(Variability);
        FoxCount += 1;
    }

    private void FoxesEatRabbitsInWarren(int WarrenX, int WarrenY) {
        int FoodConsumed;
        int PercentToEat;
        double Dist;
        int RabbitsToEat;
        int RabbitCountAtStartOfPeriod = Landscape[WarrenX][WarrenY].Warren.GetRabbitCount();
        for (int FoxX = 0; FoxX < LandscapeSize; FoxX++) {
            for (int FoxY = 0; FoxY < LandscapeSize; FoxY++) {
                if (Landscape[FoxX][FoxY].Fox != null) {
                    Dist = DistanceBetween(FoxX, FoxY, WarrenX, WarrenY);
                    if (Dist <= 3.5) {
                        PercentToEat = 20;
                    } else if (Dist <= 7) {
                        PercentToEat = 10;
                    } else {
                        PercentToEat = 0;
                    }
                    RabbitsToEat = (int) (Math.round((PercentToEat * RabbitCountAtStartOfPeriod / 100.0)));
                    FoodConsumed = Landscape[WarrenX][WarrenY].Warren.EatRabbits(RabbitsToEat);
                    Landscape[FoxX][FoxY].Fox.GiveFood(FoodConsumed);
                    if (ShowDetail) {
                        Console.println("  " + FoodConsumed + " rabbits eaten by fox at (" + FoxX + "," + FoxY + ").");
                    }
                }
            }
        }
    }

    private double DistanceBetween(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private void DrawLandscape() {
        Console.println();
        Console.println("TIME PERIOD: " + TimePeriod);
        Console.println();
        Console.print("    ");
        for (int x = 0; x < LandscapeSize; x++) {
            if (x < 10) {
                Console.print(" ");
            }
            Console.print(x + " |");
        }
        Console.println();
        for (int x = 0; x < LandscapeSize * 4 + 4; x++) {
            Console.print("-");
        }
        Console.println();
        for (int y = 0; y < LandscapeSize; y++) {
            if (y < 10) {
                Console.print(" ");
            }
            Console.print(" " + y + "|");
            for (int x = 0; x < LandscapeSize; x++) {
                if (Landscape[x][y].Warren != null) {
                    if (Landscape[x][y].Warren.GetRabbitCount() < 10) {
                        Console.print(" ");
                    }
                    Console.print(Landscape[x][y].Warren.GetRabbitCount());
                } else {
                    Console.print("  ");
                }
                if (Landscape[x][y].Fox != null) {
                    Console.print("F");
                } else {
                    Console.print(" ");
                }
                Console.print("|");
            }
            Console.println();
        }
    }
}

class Warren {

    private final int MaxRabbitsInWarren = 99;
    private Rabbit[] Rabbits;
    private int RabbitCount = 0;
    private int PeriodsRun = 0;
    private Boolean AlreadySpread = false;
    private int Variability;
    private static Random Rnd = new Random();

    public Warren(int Variability) {
        this.Variability = Variability;
        Rabbits = new Rabbit[MaxRabbitsInWarren];
        RabbitCount = (int) (CalculateRandomValue((int) (MaxRabbitsInWarren / 4), Variability));
        for (int r = 0; r < RabbitCount; r++) {
            Rabbits[r] = new Rabbit(Variability);
        }
    }

    public Warren(int Variability, int RabbitCount) {
        this.Variability = Variability;
        this.RabbitCount = RabbitCount;
        Rabbits = new Rabbit[MaxRabbitsInWarren];
        for (int r = 0; r < RabbitCount; r++) {
            Rabbits[r] = new Rabbit(Variability);
        }
    }

    private double CalculateRandomValue(int BaseValue, int Variability) {
        return BaseValue - (BaseValue * Variability / 100) + (BaseValue * Rnd.nextInt((Variability * 2) + 1) / 100);
    }

    public int GetRabbitCount() {
        return RabbitCount;
    }

    public Boolean NeedToCreateNewWarren() {
        if (RabbitCount == MaxRabbitsInWarren && !AlreadySpread) {
            AlreadySpread = true;
            return true;
        } else {
            return false;
        }
    }

    public Boolean WarrenHasDiedOut() {
        if (RabbitCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void AdvanceGeneration(Boolean ShowDetail) {
        PeriodsRun += 1;
        if (RabbitCount > 0) {
            KillByOtherFactors(ShowDetail);
        }
        if (RabbitCount > 0) {
            AgeRabbits(ShowDetail);
        }
        if (RabbitCount > 0 && RabbitCount <= MaxRabbitsInWarren) {
            if (ContainsMales()) {
                MateRabbits(ShowDetail);

            }
        }
        if (RabbitCount == 0 && ShowDetail) {
            Console.println("  All rabbits in warren are dead");
        }
    }

    public int EatRabbits(int RabbitsToEat) {
        int DeathCount = 0;
        int RabbitNumber;
        if (RabbitsToEat > RabbitCount) {
            RabbitsToEat = RabbitCount;
        }
        while (DeathCount < RabbitsToEat) {
            RabbitNumber = Rnd.nextInt(RabbitCount);
            if (Rabbits[RabbitNumber] != null) {
                Rabbits[RabbitNumber] = null;
                DeathCount += 1;
            }
        }
        CompressRabbitList(DeathCount);
        return RabbitsToEat;
    }

    private void KillByOtherFactors(Boolean ShowDetail) {
        int DeathCount = 0;
        for (int r = 0; r < RabbitCount; r++) {
            if (Rabbits[r].CheckIfKilledByOtherFactor()) {
                Rabbits[r] = null;
                DeathCount += 1;
            }
        }
        CompressRabbitList(DeathCount);
        if (ShowDetail) {
            Console.println("  " + DeathCount + " rabbits killed by other factors.");
        }
    }

    private void AgeRabbits(Boolean ShowDetail) {
        int DeathCount = 0;
        for (int r = 0; r < RabbitCount; r++) {
            Rabbits[r].CalculateNewAge();
            if (Rabbits[r].CheckIfDead()) {
                Rabbits[r] = null;
                DeathCount += 1;
            }
        }
        CompressRabbitList(DeathCount);
        if (ShowDetail) {
            Console.println("  " + DeathCount + " rabbits die of old age.");
        }
    }

    private void MateRabbits(Boolean ShowDetail) {
        int Mate = 0;
        int Babies = 0;
        double CombinedReproductionRate;
        for (int r = 0; r < RabbitCount; r++) {
            if (Rabbits[r].IsFemale() && RabbitCount + Babies < MaxRabbitsInWarren) {
                do {
                    Mate = Rnd.nextInt(RabbitCount);
                } while (Mate == r || Rabbits[Mate].IsFemale());
                CombinedReproductionRate = (Rabbits[r].GetReproductionRate() + Rabbits[Mate].GetReproductionRate()) / 2;
                if (CombinedReproductionRate >= 1) {
                    Rabbits[RabbitCount + Babies] = new Rabbit(Variability, CombinedReproductionRate);
                    Babies += 1;
                }
            }
        }
        RabbitCount = RabbitCount + Babies;
        if (ShowDetail) {
            Console.println("  " + Babies + " baby rabbits born.");
        }
    }

    private void CompressRabbitList(int DeathCount) {
        if (DeathCount > 0) {
            int ShiftTo = 0;
            int ShiftFrom = 0;
            while (ShiftTo < RabbitCount - DeathCount) {
                while (Rabbits[ShiftFrom] == null) {
                    ShiftFrom += 1;
                }
                if (ShiftTo != ShiftFrom) {
                    Rabbits[ShiftTo] = Rabbits[ShiftFrom];
                }
                ShiftTo += 1;
                ShiftFrom += 1;
            }
            RabbitCount = RabbitCount - DeathCount;
        }
    }

    private Boolean ContainsMales() {
        Boolean Males = false;
        for (int r = 0; r < RabbitCount; r++) {
            if (!Rabbits[r].IsFemale()) {
                Males = true;
            }
        }
        return Males;
    }

    public void Inspect() {
        Console.println("Periods Run " + PeriodsRun + " Size " + RabbitCount);
    }

    public void ListRabbits() {
        if (RabbitCount > 0) {
            for (int r = 0; r < RabbitCount; r++) {
                Rabbits[r].Inspect();
            }
        }
    }
}

class Animal {

    protected double NaturalLifespan;
    protected int ID;
    protected static int NextID = 1;
    protected int Age = 0;
    protected double ProbabilityOfDeathOtherCauses;
    protected Boolean IsAlive;
    protected static Random Rnd = new Random();

    public Animal(int AvgLifespan, double AvgProbabilityOfDeathOtherCauses, int Variability) {
        NaturalLifespan = AvgLifespan * CalculateRandomValue(100, Variability) / 100;
        ProbabilityOfDeathOtherCauses = AvgProbabilityOfDeathOtherCauses * CalculateRandomValue(100, Variability) / 100;
        IsAlive = true;
        ID = NextID;
        NextID += 1;
    }

    public void CalculateNewAge() {
        Age += 1;
        if (Age >= NaturalLifespan) {
            IsAlive = false;
        }
    }

    public Boolean CheckIfDead() {
        return !IsAlive;
    }

    public void Inspect() {
        Console.print("  ID " + ID + " ");
        Console.print("Age " + Age + " ");
        Console.print("LS " + (int) NaturalLifespan + " ");
        Console.print("Pr dth " + Math.round(ProbabilityOfDeathOtherCauses * 100) / 100.0 + " ");
    }

    public Boolean CheckIfKilledByOtherFactor() {
        if (Rnd.nextInt(100) < ProbabilityOfDeathOtherCauses * 100) {
            IsAlive = false;
            return true;
        } else {
            return false;
        }
    }

    protected double CalculateRandomValue(int BaseValue, int Variability) {
        return BaseValue - (BaseValue * Variability / 100) + (BaseValue * Rnd.nextInt((Variability * 2) + 1) / 100);
    }
}

class Fox extends Animal {

    private int FoodUnitsNeeded = 10;
    private int FoodUnitsConsumedThisPeriod = 0;
    private static final int DefaultLifespan = 7;
    private static final double DefaultProbabilityDeathOtherCauses = 0.1;

    public Fox(int Variability) {
        super(DefaultLifespan, DefaultProbabilityDeathOtherCauses, Variability);
        FoodUnitsNeeded = (int) (10 * this.CalculateRandomValue(100, Variability) / 100);
    }

    public void AdvanceGeneration(Boolean ShowDetail) {
        if (FoodUnitsConsumedThisPeriod == 0) {
            IsAlive = false;
            if (ShowDetail) {
                Console.println("  Fox dies as has eaten no food this period.");
            }
        } else {
            if (CheckIfKilledByOtherFactor()) {
                IsAlive = false;
                if (ShowDetail) {
                    Console.println("  Fox killed by other factor.");
                }
            } else {
                if (FoodUnitsConsumedThisPeriod < FoodUnitsNeeded) {
                    CalculateNewAge();
                    if (ShowDetail) {
                        Console.println("  Fox ages further due to lack of food.");
                    }
                }
                CalculateNewAge();
                if (!IsAlive) {
                    if (ShowDetail) {
                        Console.println("  Fox has died of old age.");
                    }
                }
            }
        }
    }

    public void ResetFoodConsumed() {
        FoodUnitsConsumedThisPeriod = 0;
    }

    public Boolean ReproduceThisPeriod() {
        final double ReproductionProbability = 0.25;
        if (Rnd.nextInt(100) < ReproductionProbability * 100) {
            return true;
        } else {
            return false;
        }
    }

    public void GiveFood(int FoodUnits) {
        FoodUnitsConsumedThisPeriod = FoodUnitsConsumedThisPeriod + FoodUnits;
    }

    @Override
    public void Inspect() {
        super.Inspect();
        Console.print("Food needed " + FoodUnitsNeeded + " ");
        Console.print("Food eaten " + FoodUnitsConsumedThisPeriod + " ");
        Console.println();
    }
}

class Rabbit extends Animal {

    private enum Genders {

        Male, Female
    };

    private double ReproductionRate;
    private final double DefaultReproductionRate = 1.2;
    private static final int DefaultLifespan = 4;
    private static final double DefaultProbabilityDeathOtherCauses = 0.05;
    private Genders Gender;

    public Rabbit(int Variability) {
        super(DefaultLifespan, DefaultProbabilityDeathOtherCauses, Variability);
        ReproductionRate = DefaultReproductionRate * CalculateRandomValue(100, Variability) / 100;
        if (Rnd.nextInt(100) < 50) {
            Gender = Genders.Male;
        } else {
            Gender = Genders.Female;
        }
    }

    public Rabbit(int Variability, double ParentsReproductionRate) {
        super(DefaultLifespan, DefaultProbabilityDeathOtherCauses, Variability);
        ReproductionRate = ParentsReproductionRate * CalculateRandomValue(100, Variability) / 100;
        if (Rnd.nextInt(100) < 50) {
            Gender = Genders.Male;
        } else {
            Gender = Genders.Female;
        }
    }

    @Override
    public void Inspect() {
        super.Inspect();
        Console.print("Rep rate " + Math.round(ReproductionRate * 10) / 10.0 + " ");
        if (Gender == Genders.Female) {
            Console.println("Gender Female");
        } else {
            Console.println("Gender Male");
        }
    }

    public Boolean IsFemale() {
        if (Gender == Genders.Female) {
            return true;
        } else {
            return false;
        }
    }

    public double GetReproductionRate() {
        return ReproductionRate;
    }
}
