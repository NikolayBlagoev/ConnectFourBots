import sys
import Exceptional


class SmartBot:
    __SCORE_FOR_2_YOUR_OWN = 1
    __SCORE_FOR_3_YOUR_OWN = 7
    __SCORE_FOR_2_OPPONENT = 4
    __SCORE_FOR_3_OPPONENT = 15
    __SCORE_FOR_WINNING = sys.maxsize
    __SCORE_FOR_PREVENTING_LOSS = 10000
    __SCORE_FOR_CAUSING_LOSS_NEXT_TURN = -1000
    __SCORE_FOR_MIDDLE = 10
    __SCORE_FOR_SECOND_TO_MIDDLE = 2

    __ANSI_RED = '\33[91m'
    __ANSI_BLUE = '\33[34m'
    __ANSI_WHITE = '\33[37m'

    def __init__(self, start_first):
        self.__board = [[0] * 7 for _ in range(6)]
        self.__turns = 0
        self.__game_over = False
        self.__game_conclusion = -2

        if start_first:
            self.__board[0][3] = 2
            self.__turns += 1

    def __valid_y(self, x):
        if (not self.__bounds_check(4, x)) or self.__board[5][x] != 0:
            return -1
        y = 0
        while y < 6:
            if self.__board[y][x] == 0:
                return y
            y += 1

        return -1

    def check_victory_condition(self, y, x):
        disc = self.__board[y][x]
        disc_ret, count = self.__get_count(-1, 0, y, x)
        if count >= 4:
            return True

        if count == 3 and disc_ret == disc:
            return True

        disc_ret1, count1 = self.__get_count(-1, -1, y, x)
        disc_ret2, count2 = self.__get_count(+1, +1, y, x)
        if self.__victory_checker_helper(disc_ret1, count1, disc_ret2, count2, disc):
            return True

        disc_ret1, count1 = self.__get_count(+1, -1, y, x)
        disc_ret2, count2 = self.__get_count(-1, +1, y, x)
        if self.__victory_checker_helper(disc_ret1, count1, disc_ret2, count2, disc):
            return True

        disc_ret1, count1 = self.__get_count(0, -1, y, x)
        disc_ret2, count2 = self.__get_count(0, +1, y, x)
        if self.__victory_checker_helper(disc_ret1, count1, disc_ret2, count2, disc):
            return True
        return False

    def __victory_checker_helper(self, disc_ret1, count1, disc_ret2, count2, disc):
        if disc_ret1 == disc_ret2 and disc_ret1 == disc:
            return count1 + count2
        else:
            if count1 >= 4 or count2 >= 4:
                return True
            if (disc_ret1 == disc and count1 == 3) or (disc_ret2 == disc and count2 == 3):
                return True
        return False

    def choose_next_move(self, player_column):
        if self.__game_over:
            raise Exceptional.GameOverException("Game has finished already")

        if player_column > 6 or player_column < 0:
            raise Exceptional.InvalidMoveException("Invalid move")

        best_move = -1
        best_y = -1
        best_score = -1 * sys.maxsize
        put_y = self.__valid_y(player_column)
        if put_y == -1:
            raise Exceptional.InvalidMoveException("Invalid Move")
        self.__board[put_y][player_column] = 1
        self.__turns += 1
        if self.check_victory_condition(put_y, player_column):
            self.__game_over = True
            self.__game_conclusion = -1
            raise Exceptional.GameOverException("Game has finished: player won")
        if self.__turns >= 42:
            self.__game_over = True
            self.__game_conclusion = 0
            raise Exceptional.GameOverException("It is a tie")
        i = 0
        while i < 7:
            put_y = self.__valid_y(i)
            if put_y != -1:
                has_won, score = self.get_score(put_y, i, 2, 1)
                # print("SCORE FOR " + str(i) + " " + str(score))
                if has_won:
                    self.__board[put_y][i] = 2
                    self.__game_over = True
                    self.__game_conclusion = 1
                    return i
                if best_score < score:
                    best_move = i
                    best_score = score
                    best_y = put_y
            i += 1
        self.__turns += 1
        self.__board[best_y][best_move] = 2
        if self.__turns >= 42:
            self.__game_over = True
            self.__game_conclusion = 0
            raise Exceptional.GameOverException("It is a tie")
        return best_move

    @staticmethod
    def __bounds_check(y, x):
        return (y > -1) and (x > -1) and (y < 6) and (x < 7)

    def __get_count(self, dy, dx, y, x):
        count = 1
        disc = 0
        if self.__bounds_check(y + dy, x + dx):
            disc = self.__board[y + dy][x + dx]
        if disc == 0:
            return 0, 0
        y = y + dy + dy
        x = x + dx + dx

        while self.__bounds_check(y, x) and self.__board[y][x] == disc and count < 4:
            count = count + 1
            y = y + dy
            x = x + dx
        # print(str(count) + " 141 " + str(disc))
        return disc, count

    def __get_score_helper(self, response_count, response_disc, disc, x):
        if response_count >= 3 and response_disc == disc:
            return True, self.__SCORE_FOR_WINNING
        elif response_count >= 3 and response_count != disc:
            return False, self.__SCORE_FOR_PREVENTING_LOSS
        elif response_count == 2 and response_disc == disc:
            return False, self.__SCORE_FOR_3_YOUR_OWN
        elif response_count == 2 and response_disc != disc:
            # print(str(disc) + " " + str(response_count) + " 152 " + str(response_disc))
            return False, self.__SCORE_FOR_3_OPPONENT
        elif response_count == 1 and response_disc == disc:
            return False, self.__SCORE_FOR_2_YOUR_OWN
        elif response_count == 1 and response_disc != disc:
            return False, self.__SCORE_FOR_2_OPPONENT
        elif response_count <= 0:
            return False, 0
        else:
            return False, -1001

    def get_score(self, y, x, disc, other_disc):
        can_win, score_ret = self.__get_score_rec(y, x, disc, other_disc)
        if can_win:
            return can_win, score_ret
        if y < 5:
            can_win2, score_ret2 = self.__get_score_rec(y + 1, x, other_disc, disc)
            if can_win2:
                return can_win, score_ret + self.__SCORE_FOR_CAUSING_LOSS_NEXT_TURN
            return can_win, score_ret
        else:
            return can_win, score_ret

    def __helper_get_score_rec(self, init_dy, init_dx, y, x, disc):
        disc_count1, response_count1 = self.__get_count(init_dy, init_dx, y, x)
        disc_count2, response_count2 = self.__get_count(-1 * init_dy, -1 * init_dx, y, x)
        # print(str(init_dx) + " " + str(init_dy) + " " + str(disc_count1) + " " + str(disc_count2))
        if disc_count1 == disc_count2:
            can_win, score_ret = self.__get_score_helper(response_count1 + response_count2, disc_count1, disc, x)
            return can_win, score_ret
        else:

            can_win, score_ret1 = self.__get_score_helper(response_count1, disc_count1, disc, x)
            # print(str(init_dy) + " 184 " + str(disc_count1) + " " + str(score_ret1))
            if can_win:
                return can_win, score_ret1
            can_win, score_ret2 = self.__get_score_helper(response_count2, disc_count2, disc, x)
            # print(str(init_dy) + " 188 " + str(disc_count2) + " " + str(score_ret2))
            if can_win:
                return can_win, score_ret2
            return can_win, score_ret1 + score_ret2

    def __get_score_rec(self, y, x, disc, other_disc):
        score = 0

        # Down check
        disc_count, response_count = self.__get_count(-1, 0, y, x)
        can_win, score_ret = self.__get_score_helper(response_count, disc_count, disc, x)
        # print("DOWN " + str(score_ret))
        if can_win:
            return can_win, score_ret
        score += score_ret

        # Diagonal 1
        can_win, score_ret = self.__helper_get_score_rec(-1, -1, y, x, disc)
        # print("DIAG 1 " + str(score_ret))
        if can_win:
            return can_win, score_ret
        score += score_ret

        # Diagonal 2
        can_win, score_ret = self.__helper_get_score_rec(+1, -1, y, x, disc)
        # print("DIAG 2 " + str(score_ret))
        if can_win:
            return can_win, score_ret
        score += score_ret

        #  Right Left
        can_win, score_ret = self.__helper_get_score_rec(0, -1, y, x, disc)
        # print("RL " + str(score_ret))
        if can_win:
            return can_win, score_ret
        score += score_ret

        if x == 3:
            score += self.__SCORE_FOR_MIDDLE
        if x == 2 or x == 4:
            score += self.__SCORE_FOR_SECOND_TO_MIDDLE
        return False, score

    def display_board(self):
        y = 12
        while y > -1:

            x = 0
            while x <= 21:
                if y % 2 == 0:
                    print(self.__ANSI_WHITE + "█", end="")

                else:
                    if x % 3 == 0:
                        print(self.__ANSI_WHITE + "█", end="")
                    else:
                        if self.__board[y // 2][x // 3] == 0:
                            print(self.__ANSI_WHITE + " ", end="")
                        elif self.__board[y // 2][x // 3] == 1:
                            print(self.__ANSI_BLUE + "█", end="")
                        elif self.__board[y // 2][x // 3] == 2:
                            print(self.__ANSI_RED + "█", end="")
                        else:
                            print("ERROR")
                x = x + 1

            print()
            y = y - 1
        print(self.__ANSI_WHITE + " 1  2  3  4   5  6  7")

    def game_conclusion_get(self):
        return self.__game_conclusion

    def is_game_over(self):
        return self.__game_over

