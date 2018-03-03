from vector import *

class MatrixN(object):
    sStrPrecision = None
    def __init__(self, rows, cols, data = []):
        self.mRows = rows
        self.mCols = cols
        self.mData = []
        if len(data) > 0:
            if len(data) != rows * cols:
                raise ValueError('Data array must contain exactly ' + str(rows * cols) + ' items!')
            for i in range(rows):
                self.mData.append(VectorN(*(data[cols * i:cols * (i+1)])))
        else:
            for i in range(rows):
                self.mData.append(VectorN(*([0] * cols)))
    def __str__(self):
        s = ''
        for i in range(self.mRows):
            if i == 0:                s += '/'
            elif i == self.mRows - 1: s += '\\'
            else:                     s += '|'
            for j in range(self.mCols):
                if MatrixN.sStrPrecision == None:
                    s += str(self.mData[i][j])
                else:
                    form = "%." + str(MatrixN.sStrPrecision) + "f"
                    s += form % self.mData[i][j]
                if j != self.mCols - 1:
                    s += ', '
            if i == 0:                s += '\\'
            elif i == self.mRows - 1: s += '/'
            else:                     s += '|'
            s += '\n'
        return s
    def __getitem__(self, idx):
        if len(idx) != 2:
            raise ValueError('You must pass an x and a y value to get an index of a matrix')
        if not 0 <= idx[0] < self.mRows:
            raise IndexError('There are only ' + str(self.mRows) + ' rows, but you tried to index into row ' + str(idx[0]))
        if not 0 <= idx[1] < self.mCols:
            raise IndexError('There are only ' + str(self.mCols) + ' columns, but you tried to index into column ' + str(idx[0]))
        return self.mData[idx[0]][idx[1]]
    def __setitem__(self, idx, val):
        if len(idx) != 2:
            raise ValueError('You must pass an x and a y value to set an index of a matrix')
        if not 0 <= idx[0] < self.mRows:
            raise IndexError('There are only ' + str(self.mRows) + ' rows, but you tried to index into row ' + str(idx[0]))
        if not 0 <= idx[1] < self.mCols:
            raise IndexError('There are only ' + str(self.mCols) + ' columns, but you tried to index into column ' + str(idx[0]))
        self.mData[idx[0]][idx[1]] = float(val)
    def copy(self):
        L = []
        for i in range(self.mRows):
            for j in range(self.mCols):
                L.append(self.mData[i][j])
        return MatrixN(self.mRows, self.mCols, L)
    def getRow(self, r):
        if not 0 <= r < self.mRows:
            raise IndexError('Can only get rows between 0 and ' + str(mRows -1) + ' (inclusive), you passed: ' + str(r)) 
        return self.mData[r].copy()
    def getColumn(self, c):
        if not 0 <= c < self.mCols:
            raise IndexError('Can only get columns between 0 and ' + str(mCols -1) + ' (inclusive), you passed: ' + str(c)) 
        L = []
        for i in range(self.mRows):
            L.append(self.mData[i][c])
        return VectorN(*L)
    def setRow(self, r, vec):
        if not 0 <= r < self.mRows:
            raise IndexError('Can only set rows between 0 and ' + str(mRows -1) + ' (inclusive), you passed: ' + str(r))
        if len(vec) != self.mCols:
            raise ValueError('Row vector must have a length of ' + str(mCols))
        self.mData[r] = vec
    def setColumn(self, c, vec):
        if not 0 <= c < self.mCols:
            raise IndexError('Can only set columns between 0 and ' + str(mCols -1) + ' (inclusive), you passed: ' + str(c)) 
        if len(vec) != self.mRows:
            raise ValueError('Column vector must have a length of ' + str(mRows))
        for i in range(self.mRows):
            self.mData[i][c] = vec[i]
    def transpose(self):
        L = []
        for i in range(self.mCols):
            for j in range(self.mRows):
                L.append(self.mData[j][i])
        return MatrixN(self.mCols, self.mRows, L)
    def __mul__(self, other):
        if isinstance(other, VectorN):
            other = MatrixN(len(other), 1, other.mData)
            return (self * other).getColumn(0)
        if isinstance(other, MatrixN):
            if self.mCols != other.mRows:
                raise ValueError('For A*B, if A has n columns, B must have n rows')
            L = []
            for i in range(self.mRows):
                for j in range(other.mCols):
                    L.append(self.getRow(i).dot(other.getColumn(j)))
            return MatrixN(self.mRows, other.mCols, L)
        elif isinstance(other, float) or isinstance(other, int):
            M = self.copy()
            for i in range(self.mRows):
                for j in range(self.mCols):
                    M[i,j] *= other
            return M
        else:
            raise TypeError('A matrix can only be multiplied by a scalar, a vector, or another matrix')
    def __rmul__(self, other):
        if isinstance(other, VectorN):
            other = MatrixN(1, len(other), other.mData)
            return (other * self).getRow(0)
        elif isinstance(other, float) or isinstance(other, int):
            return self * other
        else:
            raise TypeError('A matrix can only be multiplied by a scalar, a vector, or another matrix')
    def rowSwap(self, a, b):
        tmp = self.getRow(a).copy()
        self.setRow(a, self.getRow(b))
        self.setRow(b, tmp)
    def rowScale(self, a, c):
        self.setRow(a, self.getRow(a) * c)
    def rowScaleAdd(self, src, dest, c):
        tmp = self.getRow(dest)
        tmp += self.getRow(src) * c
        self.setRow(dest, tmp)
    def inverse(self):
        if self.mRows != self.mCols:
            return None
        M = self.copy()
        I = identity(M.mRows)
        print("start")
        print(M)
        print(I)
        for i in range(M.mRows):
            maxVal = None
            pivot = 0
            for j in range(i,M.mRows):
                if maxVal == None or M.getRow(j)[i] > maxVal:
                    pivot = j
                    maxVal = M.getRow(j)[i]
            print('['+str(i)+']pivot is at', pivot, ' with value', maxVal)
            if pivot != i:
                M.rowSwap(i, pivot)
                I.rowSwap(i, pivot)
                pivot = i
            
            val = M.getRow(pivot)[i]
            if val == 0:
                return None
            if val != 1:
                M.rowScale(pivot, (1/val))
                I.rowScale(pivot, (1/val))
                val = 1
            for j in range(M.mCols):
                val2 = M.getRow(j)[i]
                if val2 != 0 and j != pivot:
                    M.rowScaleAdd(pivot, j, -val2)
                    I.rowScaleAdd(pivot, j, -val2)
            print(M)
            print(I)
        return I
def identity(dim):
    L = []
    for i in range(dim):
        L += [0] * dim
        L[i * dim + i] = 1
    return MatrixN(dim, dim, L)
def translate(x,y,z, isRightHanded=False):
    M = identity(4)
    v = VectorN(x,y,z,1)
    M.setRow(3,v)
    if isRightHanded:
        return M.transpose()
    return M
def rotateX(angle, isRightHanded=False):
    M = identity(4)
    c = math.cos(angle)
    s = math.sin(angle)
    M[1,1] = c
    M[1,2] = s
    M[2,1] = -s
    M[2,2] = c
    if isRightHanded:
        return M.transpose()
    return M
def rotateY(angle, isRightHanded=False):
    M = identity(4)
    c = math.cos(angle)
    s = math.sin(angle)
    M[0,0] = c
    M[0,2] = -s
    M[2,0] = s
    M[2,2] = c
    if isRightHanded:
        return M.transpose()
    return M
def rotateZ(angle, isRightHanded=False):
    M = identity(4)
    c = math.cos(angle)
    s = math.sin(angle)
    M[0,0] = c
    M[0,1] = s
    M[1,0] = -s
    M[1,1] = c
    if isRightHanded:
        return M.transpose()
    return M
def scale(x,y,z):
    M = identity(4)
    M[0,0] = x
    M[1,1] = y
    M[2,2] = z
    return M
if __name__ == '__main__':
    print("Matrix construction\n===================")
    a = MatrixN(4, 3)
    b = MatrixN(2, 3, (3.0145, 7.2983, "2.314", 1.9, -2, 4.37562))
    # c = MatrixN(4, 3, (3.0145, 7.2983, "2.314", 1.9, -2, 4.37562)) # ValueError: You must pass exactly 12 values
     # in the data array to populate this 4 x 3 MatrixN
    I = identity(3)
    print(I) # /1.0 0.0 0.0\
     # |0.0 1.0 0.0|
     # \0.0 0.0 1.0/
    print(a) # /0.0 0.0 0.0\
     # |0.0 0.0 0.0|
     # |0.0 0.0 0.0|
     # \0.0 0.0 0.0/
    print("Item Accessing\n==============")
    print("b[0, 0] = " + str(b[0, 0])) # b[0, 0] = 3.0145
    # print("b[10, 4] = " + str(b[10, 4])) # IndexError: list index out of range
    print("b[1, 2] = " + str(b[1, 2]) + "\n") # b[1, 2] = 4.37562
    c = a.copy()
    a[0, 2] = 99
    a[1, 0] = "100.2"
    a[3, 1] = 101.99999
    print(a) # /0.0 0.0 99.0 \
     # |100.2 0.0 0.0 |
    # |0.0 0.0 0.0 |
     # \0.0 101.99999 0.0 /
    print(c) # /0.0 0.0 0.0\
     # |0.0 0.0 0.0|
    # |0.0 0.0 0.0|
    # \0.0 0.0 0.0/
    v = a.getRow(0)
    # v = a.getRow(4) # IndexError: list index out of range
    v[0] = 123.4
    print("v = " + str(v)) # v = <Vector3: 123.4, 0.0, 99.0>
    # v = a.getColumn(2) # IndexError: list assignment index out of range
    print(a.getColumn(0)) # <Vector4: 0.0, 100.2, 0.0, 0.0>
    print(a) # /0.0 0.0 99.0 \
     # |100.2 0.0 0.0 |
    # |0.0 0.0 0.0 |
     # \0.0 101.99999 0.0 /
    b.setRow(0, VectorN(4, 5, 6))
    b.setColumn(2, VectorN(7, 8))
    # b.setRow(0, VectorN(4, 5)) # ValueError: Invalid row argument (must be a VectorN with size = 3)
    # b.setRow(2, VectorN(4, 5, 6)) # IndexError: list index out of range
    print("Multiplication\n==============")
    print(b * a.transpose()) # /693.0 400.8 0.0 509.99995\
     # /792.0 190.38 0.0 -203.99998/
    print(b.transpose()) # /4.0 1.9 \
     # |5.0 -2.0 |
     # \7.0 8.0 /
    print(b) # /4.0 5.0 7.0 \
     # \1.9 -2.0 8.0 /
    print(b * 3) # /12.0 15.0 21.0 \
     # \5.699999999999999 -6.0 24.0 /
    print(3 * b) # /12.0 15.0 21.0 \
     # \5.699999999999999 -6.0 24.0 /
    # Note: It is assumed that if you multiply a matrix * vector, you are using a right-handed system, so the vector
    # is actually a n x 1 matrix.
    # If you do vector * matrix, you are assumed to be using a left-handed system, so the vector
    # is actually a 1 x n matrix
    v = b * VectorN(5, 4, 2) # Right-handed vector
    print(v) # <Vector2: 54.0, 17.5>
    # We now want to support vector * matrix. But...our VectorN class will (currently) complain if you multiply by
    # anything other than a scalar. We *could* fix this problem by putting matrix-multiplication in VectorN.__mul__, but
    # for this lab, I want you to instead change the exception in VectorN.__mul__ from:
    # raise ValueError("....")
    # to this:
    # return NotImplemented
    # This will still error (see the line right below this), but will allow the __rmul__ method of MatrixN to be called
    # v = VectorN(2, 1) * "abc" # TypeError: can't multiply sequence by non-int of type Vector2
    v = VectorN(5, 4) * b # Left-handed vector (hint: you'll put this code in MatrixN.__rmul__)
    print(v) # <Vector3: 27.6, 17.0, 67.0>
    # @@@@@@@@@@@@@@@@@@@
    # BONUS
    # @@@@@@@@@@@@@@@@@@@
    # @@@@@@@@@@@@@@@@@@@
    # BONUS
    # @@@@@@@@@@@@@@@@@@@
    print("Inverse and Precision\n=====================")
    c = MatrixN(2, 2, (1.234567, 2.345678, 3.456789, 4.567891))
    print(c) # /1.234567 2.345678 \
     # \3.456789 4.567891 /
    MatrixN.sStrPrecision = 2 # Makes all elements of all MatrixN's display with 2
    # decimals when using MatrixN.__str__
    print(c) # /1.23 2.35\
     # \3.46 4.57/
    MatrixN.sStrPrecision = None # Makes all elements of all MatrixN's display with unlimited
    # decimals when using MatrixN.__str__
    print(c) # /1.234567 2.345678 \
     # \3.456789 4.567891 /
    print(b) # /4.0 5.0 7.0 \
     # \1.9 -2.0 8.0 /
    print(b.inverse()) # None (non-square matrices don't have an inverse)
    c = MatrixN(3, 3, (4, 2, 0, 3, 7, 0, 2, 1, 0))
    print(c.inverse()) # None (this matrix is square, but fails to find a pivot in col#3)
    c = MatrixN(3, 3, (0, 1, 2, 1, 0, 3, 4, -3, 8))
    print(c) # /0.0 1.0 2.0 \
     # |1.0 0.0 3.0 |
     # \4.0 -3.0 8.0/
    cI = c.inverse()
    print(cI) # /-4.5 7.0 -1.5 \
     # |-2.0 4.0 -1.0 |
     # \1.5 -2.0 0.5 /
    # Test the inverse
    print(c * cI) # /1.0 0.0 0.0 \
     # |0.0 1.0 0.0 |
     # \0.0 0.0 1.0 /
##    print("Matrix construction\n===================")
##    a = MatrixN(4, 3)
##    b = MatrixN(2, 3, (3.0145, 7.2983, "2.314", 1.9, -2, 4.37562))
##     # c = MatrixN(4, 3, (3.0145, 7.2983, "2.314", 1.9, -2, 4.37562)) # ValueError: You must pass exactly 12 values
##     # in the data array to populate this 4 x 3 MatrixN
##    I = identity(3)
##    print(I) # /1.0 0.0 0.0\
##     # |0.0 1.0 0.0|
##     # \0.0 0.0 1.0/
##    print(a) # /0.0 0.0 0.0\
##     # |0.0 0.0 0.0|
##     # |0.0 0.0 0.0|
##     # \0.0 0.0 0.0/
##    print("Item Accessing\n==============")
##    print("b[0, 0] = " + str(b[0, 0])) # b[0, 0] = 3.0145
##     # print("b[10, 4] = " + str(b[10, 4])) # IndexError: list index out of range
##    print("b[1, 2] = " + str(b[1, 2]) + "\n") # b[1, 2] = 4.37562
##    c = a.copy()
##    a[0, 2] = 99
##    a[1, 0] = "100.2"
##    a[3, 1] = 101.99999
##    print(a)
##    # /0.0 0.0 99.0 \
##    # |100.2 0.0 0.0 |
##    # |0.0 0.0 0.0 |
##    # \0.0 101.99999 0.0 /
##    print(c)
##     # /0.0 0.0 0.0\
##     # |0.0 0.0 0.0|
##     # |0.0 0.0 0.0|
##     # \0.0 0.0 0.0/
##    v = a.getRow(0)
##    # v = a.getRow(4) # IndexError: list index out of range
##    v[0] = 123.4
##    print("v = " + str(v)) # v = <Vector3: 123.4, 0.0, 99.0>
##    # v = a.getColumn(2) # IndexError: list assignment index out of range
##    print(a.getColumn(0)) # <Vector4: 0.0, 100.2, 0.0, 0.0>
##    print(a) # /0.0 0.0 99.0 \
##     # |100.2 0.0 0.0 |
##     # |0.0 0.0 0.0 |
##    # \0.0 101.99999 0.0 /
##    b.setRow(0, VectorN(4, 5, 6))
##    b.setColumn(2, VectorN(7, 8))
##    # b.setRow(0, VectorN(4, 5)) # ValueError: Invalid row argument (must be a VectorN with size = 3)
##    # b.setRow(2, VectorN(4, 5, 6)) # IndexError: list index out of range
##    print("Multiplication\n==============")
##    print(b * a.transpose()) # /693.0 400.8 0.0 509.99995\
##     # /792.0 190.38 0.0 -203.99998/
##    print(b.transpose()) # /4.0 1.9 \
##     # |5.0 -2.0 |
##    # \7.0 8.0 /
##    print(b) # /4.0 5.0 7.0 \
##     # \1.9 -2.0 8.0 /
##    print(b * 3) # /12.0 15.0 21.0 \
##     # \5.699999999999999 -6.0 24.0 /
##    print(3 * b) # /12.0 15.0 21.0 \
##     # \5.699999999999999 -6.0 24.0 /
##    # Note: It is assumed that if you multiply a matrix * vector, you are using a right-handed system, so the vector
##    # is actually a n x 1 matrix.
##    # If you do vector * matrix, you are assumed to be using a left-handed system, so the vector
##    # is actually a 1 x n matrix
##    v = b * VectorN(5, 4, 2) # Right-handed vector
##    print(v) # <Vector2: 54.0, 17.5>
##    # We now want to support vector * matrix. But...our VectorN class will (currently) complain if you multiply by
##    # anything other than a scalar. We *could* fix this problem by putting matrix-multiplication in VectorN.__mul__, but
##    # for this lab, I want you to instead change the exception in VectorN.__mul__ from:
##    # raise ValueError("....")
##    # to this:
##    # return NotImplemented
##    # This will still error (see the line right below this), but will allow the __rmul__ method of MatrixN to be called
##    # v = VectorN(2, 1) * "abc" # TypeError: can't multiply sequence by non-int of type Vector2
##    
##    v = VectorN(5, 4) * b # Left-handed vector (hint: you'll put this code in MatrixN.__rmul__)
##    print(v)
##    # @@@@@@@@@@@@@@@@@@@
##    # BONUS
##    # @@@@@@@@@@@@@@@@@@@
##    print("Inverse and Precision\n=====================")
##    c = MatrixN(2, 2, (1.234567, 2.345678, 3.456789, 4.567891))
##    print(c) # /1.234567 2.345678 \
##     # \3.456789 4.567891 /
##    MatrixN.sStrPrecision = 2 # Makes all elements of all MatrixN's display with 2
##    # decimals when using MatrixN.__str__
##    print(c) # /1.23 2.35\
##     # \3.46 4.57/
##    MatrixN.sStrPrecision = None # Makes all elements of all MatrixN's display with unlimited
##    # decimals when using MatrixN.__str__
##    print(c) # /1.234567 2.345678 \
##     # \3.456789 4.567891 /
##    print(b) # /4.0 5.0 7.0 \
##     # \1.9 -2.0 8.0 /
##    print(b.inverse()) # None (non-square matrices don't have an inverse)
##    c = MatrixN(3, 3, (4, 2, 0, 3, 7, 0, 2, 1, 0))
##    print(c.inverse()) # None (this matrix is square, but fails to find a pivot in col#3)
##    c = MatrixN(3, 3, (0, 1, 2, 1, 0, 3, 4, -3, 8))
##    print(c)
##     # /0.0 1.0 2.0 \
##     # |1.0 0.0 3.0 |
##     # \4.0 -3.0 8.0/
##    cI = c.inverse()
##    print(cI)
##     # /-4.5 7.0 -1.5 \
##     # |-2.0 4.0 -1.0 |
##     # \1.5 -2.0 0.5 /
##    # Test the inverse
##    print(c * cI)
##     # /1.0 0.0 0.0 \
##     # |0.0 1.0 0.0 |
##     # \0.0 0.0 1.0 /
