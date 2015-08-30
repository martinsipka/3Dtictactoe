#include <iostream>
#include <inttypes.h>
#include <cstdlib>
#include <time.h>
#include <cmath>

#define WON 10
#define DRAW 20
#define NO_RESULT 30

typedef uint64_t u64;
typedef uint16_t u16;

using namespace std;

const u64 one = 1;
const u16 n_wins = 76;

u64 white, black, both;

int game[64];
int game_index = 0;

int root_moves[64];
int n_root_moves;
int root_moves_won[64];
int root_moves_attempted[64];

bool onmove;

const u64 win_masks[n_wins] = {15ull,
                        4369ull,
                        281479271743489ull,
                        1152922604119523329ull,
                        2251816993685505ull,
                        33825ull,
                        9223376434903384065ull,
                        8738ull,
                        562958543486978ull,
                        2305845208239046658ull,
                        17476ull,
                        1125917086973956ull,
                        4611690416478093316ull,
                        281483566907400ull,
                        34952ull,
                        2251834173947912ull,
                        9223380832956186632ull,
                        1152923703634296840ull,
                        240ull,
                        4503668347895824ull,
                        36029071898968080ull,
                        9007336695791648ull,
                        18014673391583296ull,
                        4503737070518400ull,
                        36029346783166592ull,
                        3840ull,
                        72058693566333184ull,
                        576465150383489280ull,
                        144117387132666368ull,
                        288234774265332736ull,
                        72059793128294400ull,
                        576469548530665472ull,
                        61440ull,
                        281543712968704ull,
                        1152939097061330944ull,
                        9223442406135828480ull,
                        4680ull,
                        2252074725150720ull,
                        563087425937408ull,
                        2305878194122661888ull,
                        1126174851874816ull,
                        4611756388245323776ull,
                        1152956690052710400ull,
                        2252349703749632ull,
                        281612482805760ull,
                        9223512776490647552ull,
                        983040ull,
                        286326784ull,
                        2216755200ull,
                        572653568ull,
                        1145307136ull,
                        2290614272ull,
                        15728640ull,
                        251658240ull,
                        4026531840ull,
                        306708480ull,
                        64424509440ull,
                        18764712116224ull,
                        145277268787200ull,
                        37529424232448ull,
                        75058848464896ull,
                        150117696929792ull,
                        1030792151040ull,
                        16492674416640ull,
                        263882790666240ull,
                        20100446945280ull,
                        4222124650659840ull,
                        1229764173248856064ull,
                        9520891087237939200ull,
                        2459528346497712128ull,
                        4919056692995424256ull,
                        9838113385990848512ull,
                        67553994410557440ull,
                        1080863910568919040ull,
                        17293822569102704640ull,
                        1317302891005870080ull};

const u64 DeBruijn_64 = 0x3F79D71B4CB0A89ULL;

const int BSF[64] = {0,
                47,
                1,
                56,
                48,
                27,
                2,
                60,
                57,
                49,
                41,
                37,
                28,
                16,
                3,
                61,
                54,
                58,
                35,
                52,
                50,
                42,
                21,
                44,
                38,
                32,
                29,
                23,
                17,
                11,
                4,
                62,
                46,
                55,
                26,
                59,
                40,
                36,
                15,
                53,
                34,
                51,
                20,
                43,
                31,
                22,
                10,
                45,
                25,
                39,
                14,
                33,
                19,
                30,
                9,
                24,
                13,
                18,
                8,
                12,
                7,
                6,
                5,
                63};

inline unsigned bsf_index(u64 b) {
    b ^= (b - 1);
    return (b * DeBruijn_64) >> 58;
}

int lsb(u64 b) {
    return BSF[bsf_index(b)];
}

int is_won(bool clr)
{
    u64 Us = clr ? white : black;
    bool drawish = true;
    for (int i=0; i<n_wins; i++)
    {
        if((Us & win_masks[i]) == win_masks[i])
            return WON;
        if (drawish && !((white & win_masks[i]) && (black & win_masks[i])))
            drawish = false;
    }

    return drawish ? DRAW : NO_RESULT;
}

void make_move(int n)
{
    game[game_index] = n;
    both |= one << n;
    if (game_index & 1)
        black |= one << n;
    else
        white |= one << n;
    game_index++;
    onmove = !onmove;
}

void unmake_move()
{
    game_index--;
    int n = game[game_index];
    both ^= one << n;
    if (game_index & 1)
        black ^= one << n;
    else
        white ^= one << n;

    onmove = !onmove;
}
int randomize(int n)
{
    int won=0;
    int temp;
    bool us = !onmove;
    int previndex = game_index;
    bool rand_bad;
    for (int i=0; i<n; i++)
    {
        int boolwon = is_won(!onmove);
        while(boolwon == NO_RESULT)
        {
            rand_bad = true;
            while(rand_bad)
            {
                temp = rand() % 64;
                if(!((one << temp) & both))
                {
                    make_move(temp);
                    rand_bad = false;
                }
            }
            boolwon = is_won(!onmove);
        }
        if(boolwon == DRAW)
            won++;
        else if((!onmove) == us)
            won+=2;
        while (game_index>previndex)
        {
            unmake_move();
        }
    }
    return won;
}

int _search(int board[4][4][4], int stm, int playouts)
{
    //srand(time(NULL));
    white = black = both = 0;
    for (int i=0; i<4; i++)
        for (int j=0; j<4; j++)
            for (int k=0; k<4; k++)
    {
        u64 pt = one << (16*i+4*j+k);
        if (board[i][j][k])
            both |= pt;

        if (board[i][j][k] == 1)
            white |= pt;

        if (board[i][j][k] == 5)
            black |= pt;
    }

    onmove = stm == 1 ? true : false;
    game_index = onmove ? 0 : 1;

    u64 _both = ~both;
    int index, best;
    n_root_moves = 0;
    while(_both)
    {
        index = lsb(_both & ~(_both-1));
        root_moves[n_root_moves] = index;
        n_root_moves++;
        _both &= (_both-1);
    }

    for (int i=0; i<64; i++)
    {
        root_moves_won[i] = root_moves_attempted[i] = 0;
    }

    int n = 0;
    float log_playouts = log(playouts);

    while (n < playouts)
    {
        int _index = 0;
        float best = -1000.0;
        for (int i=0; i<n_root_moves; i++)
            {
                float curr = 0.5*float(root_moves_won[i])/float(root_moves_attempted[i]+1) + 1.414 * sqrt(log_playouts/float(root_moves_attempted[i]+1));
                if (curr > best)
                {
                    best = curr;
                    _index = i;
                }
            }

        make_move(root_moves[_index]);
        root_moves_won[_index] += randomize(5);
        root_moves_attempted[_index] += 5;
        unmake_move();

        n+=5;
    }

    int _index = 0;
    float _best = -1000.0;
    for (int i=0; i<n_root_moves; i++)
        {
            float curr = 0.5*float(root_moves_won[i])/float(root_moves_attempted[i]);
            if (curr > _best)
            {
                _best = curr;
                _index = i;
            }
        }
    return root_moves[_index];
}