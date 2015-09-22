#include <inttypes.h>
#include <cstdlib>
#include <cmath>
#include <jni.h>

extern "C" {

typedef uint64_t u64;
typedef uint16_t u16;

using namespace std;

const u64 one = 1;
const u16 n_wins = 76;

u64 white, black, both;

int game[65];
int game_index = 0;

int root_moves[64];
int n_root_moves;
int root_moves_won[64];
int root_moves_attempted[64];

int n_occupied_masks[2][n_wins];

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

const int masks_possible[64][7] = {{0,1,2,3,4,5,6},
                                   {0,7,8,9},
                                   {0,10,11,12},
                                   {0,13,14,15,16,17,36},
                                   {1,18,19,20},
                                   {5,7,18,21},
                                   {10,18,22,36},
                                   {14,18,23,24},
                                   {1,25,26,27},
                                   {7,25,28,36},
                                   {5,10,25,29},
                                   {14,25,30,31},
                                   {1,32,33,34,35,36,37},
                                   {7,32,38,39},
                                   {10,32,40,41},
                                   {5,14,32,42,43,44,45},
                                   {2,46,47,48},
                                   {4,8,46,49},
                                   {11,13,46,50},
                                   {15,46,51,55},
                                   {3,19,47,52},
                                   {6,9,20,21,48,49,52},
                                   {12,17,22,23,50,52,55},
                                   {16,24,51,52},
                                   {26,33,47,53},
                                   {27,28,37,38,49,53,55},
                                   {29,30,40,44,48,50,53},
                                   {31,43,51,53},
                                   {34,47,54,55},
                                   {35,39,49,54},
                                   {41,42,50,54},
                                   {45,48,51,54},
                                   {2,56,57,58},
                                   {8,13,56,59},
                                   {4,11,56,60},
                                   {15,56,61,65},
                                   {19,33,57,62},
                                   {21,23,38,44,58,59,62},
                                   {20,22,37,40,60,62,65},
                                   {24,43,61,62},
                                   {3,26,57,63},
                                   {9,17,28,30,59,63,65},
                                   {6,12,27,29,58,60,63},
                                   {16,31,61,63},
                                   {34,57,64,65},
                                   {39,42,59,64},
                                   {35,41,60,64},
                                   {45,58,61,64},
                                   {2,13,33,44,66,67,68},
                                   {8,38,66,69},
                                   {11,40,66,70},
                                   {4,15,37,43,66,71,75},
                                   {19,23,67,72},
                                   {21,68,69,72},
                                   {22,70,72,75},
                                   {20,24,71,72},
                                   {26,30,67,73},
                                   {28,69,73,75},
                                   {29,68,70,73},
                                   {27,31,71,73},
                                   {3,17,34,42,67,74,75},
                                   {9,39,69,74},
                                   {12,41,70,74},
                                   {6,16,35,45,68,71,74}
};

const int n_masks[64] = {7, 4, 4, 7,
                         4, 4, 4, 4,
                         4, 4, 4, 4,
                         7, 4, 4, 7,
                         4, 4, 4, 4,
                         4, 7, 7, 4,
                         4, 7, 7, 4,
                         4, 4, 4, 4,
                         4, 4, 4, 4,
                         4, 7, 7, 4,
                         4, 7, 7, 4,
                         4, 4, 4, 4,
                         7, 4, 4, 7,
                         4, 4, 4, 4,
                         4, 4, 4, 4,
                         7, 4, 4, 7};

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

bool make_move(int n)
{
    game[game_index] = n;
    both |= one << n;
    if (game_index & 1)
        black |= one << n;
    else
        white |= one << n;
    game_index++;
    onmove = !onmove;

    for (int l=0; l<n_masks[n]; l++)
    {
        n_occupied_masks[(game_index-1) & 1][masks_possible[n][l]]++;
        if (n_occupied_masks[(game_index-1) & 1][masks_possible[n][l]] == 4)
            return true;
    }
    return false;
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

int randomize(int n, int pt)
{
    int won=0;
    int temp;
    bool us = !onmove;
    int previndex = game_index;
    bool rand_bad;
    bool killed;
    bool killable;
    bool needing_defense;
    int sq_def;

    int _copy_occupied_masks[2][n_wins];

    for (int i=0; i<2; i++)
    {
        for(int j=0; j<n_wins; j++)
            _copy_occupied_masks[i][j] = n_occupied_masks[i][j];
    }

    for (int i=0; i<n; i++)
    {
        int state = 0;
        killed = false;
        while (!killed)
        {
            killable = false;
            for (int i=0; i<n_wins; i++)
            {
                if (n_occupied_masks[!onmove][i] == 3 && n_occupied_masks[onmove][i] == 0)
                {
                    killable = true;
                    break;
                }
            }
            if (killable)
                break;

            needing_defense = false;
            for (int i=0; i<n_wins; i++)
            {
                if (n_occupied_masks[onmove][i] == 3 && n_occupied_masks[!onmove][i] == 0)
                {
                    needing_defense = true;
                    sq_def = lsb(win_masks[i]&(~both));
                    break;
                }
            }

            if (needing_defense)
            {
                make_move(sq_def);
                continue;
            }

            state = 1;
            if (!(~both))
            {
                state = 10;
                break;
            }
            rand_bad = true;
            while(rand_bad)
            {
                temp = rand() % 64;
                if(!((one << temp) & both))
                {
                    killed = make_move(temp);
                    rand_bad = false;
                }
            }
        }
        if(state == 10)
            won++;
        else if(onmove == us)
            won += 2;

        while (game_index>previndex)
        {
            unmake_move();
        }

        if (i!=(n-1))
            for (int i=0; i<2; i++)
            {
                for(int j=0; j<n_wins; j++)
                    n_occupied_masks[i][j] = _copy_occupied_masks[i][j];
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

    for (int i=0; i<2; i++)
    {
        for(int j=0; j<n_wins; j++)
            n_occupied_masks[i][j] = 0;
    }

    for (int i=0; i<4; i++)
        for (int j=0; j<4; j++)
            for (int k=0; k<4; k++)
            {
                int pt = (16*i+4*j+k);
                if (!board[i][j][k])
                    continue;

                if (board[i][j][k] == 1)
                {
                    for (int l=0; l<n_masks[pt]; l++)
                        n_occupied_masks[0][masks_possible[pt][l]]++;
                }

                if (board[i][j][k] == 5)
                {
                    for (int l=0; l<n_masks[pt]; l++)
                        n_occupied_masks[1][masks_possible[pt][l]]++;
                }
            }

    int copy_occupied_masks[2][n_wins];

    for (int i=0; i<2; i++)
    {
        for(int j=0; j<n_wins; j++)
            copy_occupied_masks[i][j] = n_occupied_masks[i][j];
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

        bool k = make_move(root_moves[_index]);
        if (k)
            root_moves_won[_index] += 2*5;
        else
            root_moves_won[_index] += randomize(5, root_moves[_index]);

        root_moves_attempted[_index] += 5;
        unmake_move();

        for (int i=0; i<2; i++)
        {
            for(int j=0; j<n_wins; j++)
                n_occupied_masks[i][j] = copy_occupied_masks[i][j];
        }

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
        //cout << root_moves[i] << " " << root_moves_attempted[i] << " " << curr << endl;
    }
    //cout << root_moves[_index] << " " << root_moves_won[_index];
    return root_moves[_index];
    }

    jint Java_com_example_dtictactoe_AI_ArtificialIntelligence_getPosition(JNIEnv *env, jobject thiz,
        jobjectArray field){

        int board[4][4][4];
        for( int i = 0; i < 4; i++){
            jobjectArray twoDimens = (jobjectArray) env->GetObjectArrayElement(field, i);
            for(int j = 0; j < 4; j++){
                jintArray oneDimen = (jintArray)  env->GetObjectArrayElement(twoDimens, j);
                jint *array = env->GetIntArrayElements(oneDimen, 0);
                for(int k = 0; k < 4; k++) {
                    board[i][j][k] = array[k];
                }
            }
        }
        return _search(board, 5, 30000);;//_search();
    }

}